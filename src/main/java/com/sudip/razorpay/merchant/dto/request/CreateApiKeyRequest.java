package com.sudip.razorpay.merchant.dto.request;

import com.sudip.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
