package com.sudip.razorpay.merchant.dto.request;

import com.sudip.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(

        @NotNull(message = "Name is required")
        @Size(max = 50, message = "Name should not be more than 50 characters")
        String name,

        @Email
        @NotNull(message = "Email is required")
        String email,

        @NotNull(message = "Password is requires")
        @Size(min = 8,message = "Password should be 8 Characters long")
        String password,

        @Size(max = 50, message = "BusinessName should be more that 50 characters")
        String businessName,

        BusinessType businessType
) {
}
