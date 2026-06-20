package com.sudip.razorpay.payment.controller;

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
    UUID merchantId = UUID.fromString("c94fd399-5a29-44a6-824e-e628014568e2");  // TODO: Replace it with the merchant context

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantId, request));
  }
}
