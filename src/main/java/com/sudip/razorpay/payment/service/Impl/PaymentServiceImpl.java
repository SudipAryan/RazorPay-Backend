package com.sudip.razorpay.payment.service.Impl;

import com.sudip.razorpay.common.enums.OrderStatus;
import com.sudip.razorpay.common.enums.PaymentStatus;
import com.sudip.razorpay.common.exceptions.BusinessRuleViolationException;
import com.sudip.razorpay.common.exceptions.ResourceNotFoundException;
import com.sudip.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.sudip.razorpay.payment.dto.response.PaymentResponse;
import com.sudip.razorpay.payment.entity.OrderRecord;
import com.sudip.razorpay.payment.entity.Payment;
import com.sudip.razorpay.payment.gateway.PaymentGatewayRouter;
import com.sudip.razorpay.payment.gateway.dto.PaymentRequest;
import com.sudip.razorpay.payment.gateway.dto.PaymentResult;
import com.sudip.razorpay.payment.mapper.PaymentMapper;
import com.sudip.razorpay.payment.repository.OrderRepository;
import com.sudip.razorpay.payment.repository.PaymentRepository;
import com.sudip.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequestDto requestDto) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(requestDto.OrderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", requestDto.OrderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                    "Order cannot accept payment in status: "+order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .method(requestDto.method())
                .methodDetails(requestDto.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
                requestDto.OrderId(), merchantId,
                order.getAmount(), requestDto.method(),
                requestDto.methodDetails());
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }

        payment  = paymentRepository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toResponse(payment);
    }
}
