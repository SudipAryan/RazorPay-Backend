package com.sudip.razorpay.payment.service.Impl;

import com.sudip.razorpay.common.enums.OrderStatus;
import com.sudip.razorpay.common.enums.PaymentEvent;
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
import com.sudip.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;

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
                .idempotencyKey(UUID.randomUUID().toString())
                .methodDetails(requestDto.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
                requestDto.OrderId(), merchantId,
                order.getAmount(), requestDto.method(),
                requestDto.methodDetails());

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
//                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                log.warn("Invalid state");
                return null;
            }
        }

        payment  = paymentRepository.save(payment);
        orderRepository.save(order);

//        TODO: Send an outbox (Kafka event)

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

        if (paymentResult instanceof PaymentResult.Success success) {
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment captured, paymentID: {}", paymentId);
        } else if (paymentResult instanceof  PaymentResult.Failure failure) {
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("Payment captured, paymentID: {}", paymentId);
        }

        payment = paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve,
                                     String bankRef, String errorCode, String errorDescription) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in Authorizing state, PaymentID: {}, status: {}", paymentId, payment.getStatus());
            return;
        }
        OrderRecord orderRecord = payment.getOrder();

        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            // Auto-capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if (captureResult instanceof PaymentResult.Success success) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof PaymentResult.Failure failure) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);

        // TODO: Send an outbox (Kafka event)
    }
}
