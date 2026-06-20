package com.lp.razorpay_clone.merchant.dto.response;

import com.lp.razorpay_clone.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String keyId,
        Environment environment,
        Boolean enabled,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt
) {
}
