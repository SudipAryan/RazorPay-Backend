package com.sudip.razorpay.merchant.service;


import com.sudip.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.sudip.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, @Valid CreateApiKeyRequest request);
}
