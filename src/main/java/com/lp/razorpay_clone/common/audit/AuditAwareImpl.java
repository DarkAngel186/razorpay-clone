package com.lp.razorpay_clone.common.audit;

import com.lp.razorpay_clone.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
@RequiredArgsConstructor
public class AuditAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {

        try {
            String keyId = merchantContext.getKeyId();
            if (keyId != null && !keyId.isBlank()) {
                return Optional.of(keyId);
            }

            if (merchantContext.getMerchantId() != null) {
                return Optional.of("merchant_id: " + merchantContext.getKeyId());
            }
        } catch (Exception ignored) {
        }

        return Optional.of("SYSTEM");
    }
}
