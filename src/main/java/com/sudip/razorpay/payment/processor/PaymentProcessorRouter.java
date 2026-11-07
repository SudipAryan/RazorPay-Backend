package com.sudip.razorpay.payment.processor;

import com.sudip.razorpay.common.enums.PaymentMethods;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethods, PaymentProcessor> paymentProcessors;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor processor = paymentProcessors.get(request.methods());
        if (processor == null) {
            throw new IllegalArgumentException("No payment processor registered for method: " + request.methods());
        }
        return processor.charge(request);
    }
}
