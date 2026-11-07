package com.sudip.razorpay.payment.controller;

import com.sudip.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.sudip.razorpay.payment.dto.response.PaymentResponse;
import com.sudip.razorpay.payment.gateway.dto.PaymentRequest;
import com.sudip.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    UUID merchantId = UUID.fromString("1e32eec9-18ea-4c87-950f-d9bbb27b6fa3");  // TODO: Replace it with the merchant context

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentInitRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, request));
    }

    @PostMapping("/{paymwntId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID  paymentId) {
        return ResponseEntity.ok(paymentService.capture(merchantId, paymentId));
    }
}
