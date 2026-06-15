package com.sudip.razorpay.merchant.dto.response;

import com.sudip.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String KeyId,
        String KeySecret,
        Environment environment
) {
}
