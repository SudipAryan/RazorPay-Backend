package com.sudip.razorpay.payment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import com.sudip.razorpay.payment.dto.request.CreateOrderRequest;
import com.sudip.razorpay.payment.dto.response.OrderResponse;
import com.sudip.razorpay.payment.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    UUID merchantId = UUID.fromString("1e32eec9-18ea-4c87-950f-d9bbb27b6fa3");  // TODO: Replace it with the merchant context

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantId, request));
    }
}
