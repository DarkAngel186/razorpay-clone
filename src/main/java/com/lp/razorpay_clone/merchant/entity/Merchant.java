package com.lp.razorpay_clone.merchant.entity;

import com.lp.razorpay_clone.common.enums.BusinessType;
import com.lp.razorpay_clone.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "merchant")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, length = 200)
    String name;

    @Column(unique = true, nullable = false)
    String email;

    @Column(length = 20)
    String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    BusinessType businessType;

    @Column(length = 100)
    String businessName;

    @Column(length = 200)
    String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 200)
    MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    String gstId;

    @Column(length = 20)
    String panId;

    @Column(length = 20)
    String settlementBankAccount;

    @Column(length = 20)
    String settlementBankIfsc;

    @Column(length = 20)
    String settlementBankAccountHolderName;
}
