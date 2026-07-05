package com.sudip.razorpay.payment.service;

import com.sudip.razorpay.payment.dto.request.PaymentInitRequestDto;
import com.sudip.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequestDto requestDto);
}
