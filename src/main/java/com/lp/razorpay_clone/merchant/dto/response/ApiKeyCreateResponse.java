package com.lp.razorpay_clone.merchant.dto.response;

import com.lp.razorpay_clone.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
