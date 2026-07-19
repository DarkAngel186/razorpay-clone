package com.lp.razorpay_clone.vault.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.enums.CardBrand;
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
public class VaultCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    byte[] encryptedPan;

    @Column(nullable = false)
    byte[] encryptedDek;;

    @Column(nullable = false, length = 4)
    String lastFour;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    CardBrand brand;

    @Column(nullable = false, length = 6)
    String bin;

    @Column(nullable = false, length = 2)
    String expiryMonth;

    @Column(nullable = false, length = 4)
    String expiryYear;

    @Column(nullable = false, length = 100)
    String cardHolderName;

    LocalDateTime deletedAt;
}
