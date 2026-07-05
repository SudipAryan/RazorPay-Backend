package com.sudip.razorpay.payment.gateway;

import com.sudip.razorpay.payment.gateway.dto.PaymentRequest;
import com.sudip.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);
}
