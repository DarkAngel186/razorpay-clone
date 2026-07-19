package com.lp.razorpay_clone.merchant.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key",
        indexes = {
            @Index(name="idx_api_key_merchant", columnList = "merchant_id, environment, enabled")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;

    @Column(nullable = false, unique = true, length = 50)
    String keyId;

    @Column(nullable = false, length = 200)
    String keySecretHash;

    @Column(length = 200)
    String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    Environment environment;

    @Builder.Default
    @Column(nullable = false)
    Boolean enabled = true;

    LocalDateTime lastUsedAt;
    LocalDateTime rotatedAt;
    LocalDateTime gracePeriodExpiresAt;

    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
