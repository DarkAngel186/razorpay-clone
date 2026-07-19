package com.lp.razorpay_clone.vault.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vault_card")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CardToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(unique = true, nullable = false, length = 50)
    String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_card_id", nullable = false)
    VaultCard vaultCard;

    @Column(nullable = false)
    UUID merchantId;

    @Column(nullable = false)
    UUID customerId;

    LocalDateTime revokedAt;
}
