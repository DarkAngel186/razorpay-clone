package com.lp.razorpay_clone.operations.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "settlement")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID merchantId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gross_amount_units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "gross_currency", nullable = false))
    })
    Money grossAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "refund_amount_units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "refund_currency", nullable = false))
    })
    Money refundAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "fee_amount_units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "fee_currency", nullable = false))
    })
    Money feeAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gst_amount_units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "gst_currency", nullable = false))
    })
    Money gstAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "net_amount_units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "net_currency", nullable = false))
    })
    Money netAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SettlementStatus status;

    @Column(nullable = false, length = 100)
    String bankReference;

    LocalDateTime processedAt;
}
