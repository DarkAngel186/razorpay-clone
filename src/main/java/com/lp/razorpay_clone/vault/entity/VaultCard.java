package com.lp.razorpay_clone.vault.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "vault_card")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class VaultCard {

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
    String brand;

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
