package com.lp.razorpay_clone.merchant.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer",
        indexes = {
                @Index(name="idx_customer_merchant_id", columnList = "merchant_id"),
                @Index(name="idx_customer_email", columnList = "email")
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;

    @Column(length = 200)
    String email;

    @Column(length = 200)
    String name;

    @Column(length = 20)
    String contactNumber;

    LocalDateTime deletedAt;
}
