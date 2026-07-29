package com.sudip.razorpay.payment.processor;

import com.sudip.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}
