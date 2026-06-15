package com.sudip.razorpay.merchant.dto.response;

import com.sudip.razorpay.common.enums.BusinessType;
import com.sudip.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
