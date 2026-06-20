package com.lp.razorpay_clone.merchant.dto.request;

import com.lp.razorpay_clone.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(
        @NotBlank(message = "Name cannot be null")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @Email(message = "Invalid email format")
        @NotNull(message = "Email cannot be null")
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @Size(max = 50, message = "Business name cannot exceed 50 characters")
        String businessName,

        BusinessType businessType
) {}
