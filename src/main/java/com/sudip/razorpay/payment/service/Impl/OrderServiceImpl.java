package com.sudip.razorpay.payment.service.Impl;


import com.sudip.razorpay.common.enums.OrderStatus;
import com.sudip.razorpay.common.exceptions.DuplicateResourceException;
import com.sudip.razorpay.payment.dto.request.CreateOrderRequest;
import com.sudip.razorpay.payment.dto.response.OrderResponse;
import com.sudip.razorpay.payment.entity.OrderRecord;
import com.sudip.razorpay.payment.repository.OrderRepository;
import com.sudip.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-minutes:30}")
    private int defaultOrderExpiresMinutes;

    @Override
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

        return new OrderResponse(record.getId(),
                record.getMerchantId(),
                record.getReceipt(), record.getAmount(),
                record.getOrderStatus(), record.getAttempts(),
                record.getNotes(), record.getExpiredAt(),
                null);
    }
}
