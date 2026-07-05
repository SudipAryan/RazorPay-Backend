package com.sudip.razorpay.payment.dto.request;

import com.sudip.razorpay.common.enums.PaymentMethods;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

import java.util.UUID;

public record PaymentInitRequestDto(

        @NotNull(message = "OrderId is required")
        UUID OrderId,

        @NotNull(message = "Payment method is required")
        PaymentMethods method,

        Map<String, Object> methodDetails


) {
}
