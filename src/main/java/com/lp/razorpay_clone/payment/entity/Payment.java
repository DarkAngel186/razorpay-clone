package com.lp.razorpay_clone.payment.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "payment",
        indexes = {
                @Index(name="idx_payment_merchant_id", columnList = "merchant_id"),
                @Index(name="idx_payment_order_id", columnList = "order_id")
        })
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    OrderRecord order;

    @Column(nullable = false)
    UUID merchantId;

    @Embedded
    Money amount;

    @Column(nullable = false)
    String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentMethod paymentMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Object> methodDetails;

    String bankReference;

    String processorReference;

    String errorCode;

    String errorDescription;

    LocalDateTime authorizedAt;
    LocalDateTime capturedAt;
    LocalDateTime settledAt;
    LocalDateTime failedAt;
    LocalDateTime refundedAt;
}
