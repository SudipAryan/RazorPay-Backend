package com.sudip.razorpay.payment.processor.dto;

import com.sudip.razorpay.common.entity.Money;
import com.sudip.razorpay.common.enums.PaymentMethods;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethods methods,
        Money amount,
        Map<String, Object> methodDetails
) {
}
