package com.sudip.razorpay.payment.service.Impl;

import com.sudip.razorpay.common.enums.OrderStatus;
import com.sudip.razorpay.common.exceptions.BusinessRuleViolationException;
import com.sudip.razorpay.common.exceptions.DuplicateResourceException;
import com.sudip.razorpay.common.exceptions.ResourceNotFoundException;
import com.sudip.razorpay.payment.dto.request.CreateOrderRequest;
import com.sudip.razorpay.payment.dto.response.OrderResponse;
import com.sudip.razorpay.payment.dto.response.PaymentResponse;
import com.sudip.razorpay.payment.entity.OrderRecord;
import com.sudip.razorpay.payment.entity.Payment;
import com.sudip.razorpay.payment.mapper.OrderMapper;
import com.sudip.razorpay.payment.mapper.PaymentMapper;
import com.sudip.razorpay.payment.repository.OrderRepository;
import com.sudip.razorpay.payment.repository.PaymentRepository;
import com.sudip.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-minutes:30}")
    private int defaultOrderExpiresMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {
        if (request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())) {
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATED",
                    "Order with receipt already exists");
        }

        OrderRecord record = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())

                .merchantId(merchantId)
                .orderStatus(OrderStatus.CREATED)
                .expiredAt(request.expiresAt() != null ? request.expiresAt():
                        LocalDateTime.now().plusMinutes(defaultOrderExpiresMinutes))
                .build();


        record = orderRepository.save(record);

//        TODO: Publish Kafka event about order creation

        return orderMapper.toResponse(record);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELED || order.getOrderStatus() == OrderStatus.PAID) {
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCLE",
                    "Cannot cancel order with status " + order.getOrderStatus().name());
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_Id(orderId);

        return paymentMapper.toResponseList(paymentList);
    }
}
