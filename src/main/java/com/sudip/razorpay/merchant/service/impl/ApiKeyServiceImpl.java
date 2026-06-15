package com.sudip.razorpay.merchant.service.impl;

import com.sudip.razorpay.common.exceptions.ResourceNotFoundException;
import com.sudip.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.sudip.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.sudip.razorpay.merchant.entity.ApiKey;
import com.sudip.razorpay.merchant.entity.Merchant;
import com.sudip.razorpay.merchant.repository.ApiKeyRepository;
import com.sudip.razorpay.merchant.repository.MerchantRepository;
import com.sudip.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId).
                orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_"+request.environment().name().toUpperCase()+"big_random_string";
        String rawSecret = "big_random_secret"; // TODO: replace with cryptographic hex

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) // TODO: Encode with BcryptPassword encoder
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }
}


