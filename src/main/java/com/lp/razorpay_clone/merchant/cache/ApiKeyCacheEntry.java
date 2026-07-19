package com.lp.razorpay_clone.merchant.cache;

import com.lp.razorpay_clone.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(
      String keyId,
      String keySecretHash,
      String previousKeySecretHash,
      LocalDateTime gracePeriodExpiresAt,
      UUID merchantId,
      Environment environment,
      boolean isEnabled
) {
    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
