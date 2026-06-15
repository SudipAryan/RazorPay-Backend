package com.sudip.razorpay.merchant.service;

import com.sudip.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.sudip.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
