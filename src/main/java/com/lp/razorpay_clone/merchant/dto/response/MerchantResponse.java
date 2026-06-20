package com.lp.razorpay_clone.merchant.dto.response;

import com.lp.razorpay_clone.common.enums.BusinessType;
import com.lp.razorpay_clone.common.enums.MerchantStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MerchantResponse(

        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus status
) {}
