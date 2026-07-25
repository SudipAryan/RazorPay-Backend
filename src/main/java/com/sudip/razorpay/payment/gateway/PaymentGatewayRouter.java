package com.sudip.razorpay.payment.gateway;

import com.sudip.razorpay.common.enums.PaymentMethods;
import com.sudip.razorpay.payment.gateway.dto.PaymentRequest;
import com.sudip.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethods, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest request) {
        PaymentAdapter adapter = paymentAdapters.get(request.method());
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter registered for method: " + request.method());
        }
        return adapter.initiate(request);
    }

    public PaymentResult capture(PaymentMethods method, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapters.get(method);
        if (adapter == null) {
            throw new IllegalArgumentException("No payment adapter registered for method: " + method);
        }
        return adapter.capture(paymentId);
    }
}
