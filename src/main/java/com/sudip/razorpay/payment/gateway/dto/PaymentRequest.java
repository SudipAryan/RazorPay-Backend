package com.sudip.razorpay.payment.gateway.dto;

import com.sudip.razorpay.common.entity.Money;
import com.sudip.razorpay.common.enums.PaymentMethods;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethods method,
        Map<String, Object> methodDetails
) {
}
