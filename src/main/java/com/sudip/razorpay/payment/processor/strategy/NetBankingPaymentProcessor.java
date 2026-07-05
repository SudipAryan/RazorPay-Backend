package com.sudip.razorpay.payment.processor.strategy;

import com.sudip.razorpay.payment.processor.PaymentProcessor;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse process(PaymentProcessorRequest request) {
        return null;
    }
}
