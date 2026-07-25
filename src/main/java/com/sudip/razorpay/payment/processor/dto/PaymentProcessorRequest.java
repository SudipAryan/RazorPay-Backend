package com.sudip.razorpay.payment.processor.dto;

import com.sudip.razorpay.common.entity.Money;
import com.sudip.razorpay.common.enums.PaymentMethods;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,
        UUID paymentId,
        PaymentMethods methods,
        Money amount,
        String pan,
        String expiry,
        Map<String, Object> methodDetails
) {

    public static PaymentProcessorRequest card(UUID paymentId ,String pan, String expiry, Money amount, Map<String, Object> details) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, PaymentMethods.CARD, amount, pan, expiry, details);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethods method, Money amount, Map<String, Object> details) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, method, amount,null, null, details);
    }

}
