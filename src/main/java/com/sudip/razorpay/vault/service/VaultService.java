package com.sudip.razorpay.vault.service;

import com.sudip.razorpay.common.entity.Money;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.sudip.razorpay.vault.dto.request.TokenizeRequest;
import com.sudip.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
