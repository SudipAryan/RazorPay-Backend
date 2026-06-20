package com.sudip.razorpay.payment.service;

import com.sudip.razorpay.payment.dto.request.CreateOrderRequest;
import com.sudip.razorpay.payment.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}
