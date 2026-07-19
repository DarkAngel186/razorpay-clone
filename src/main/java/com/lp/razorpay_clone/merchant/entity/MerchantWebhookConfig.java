package com.lp.razorpay_clone.merchant.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
        indexes = {
                @Index(name="idx_webhook_merchant_id", columnList = "merchant_id, enabled")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;

    @Column(nullable = false, length = 500)
    String targetUrl;

    @Column(length = 255)
    String webhookSecretHash;

    @Column(nullable = false)
    Boolean enabled = true;

    @Column(length = 255)
    String eventTypes;
}
