package com.sudip.razorpay.merchant.service;

import com.sudip.razorpay.merchant.dto.request.LoginRequest;
import com.sudip.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.sudip.razorpay.merchant.dto.response.LoginResponse;
import com.sudip.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(@Valid LoginRequest request);
}
