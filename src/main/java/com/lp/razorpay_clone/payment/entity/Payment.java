package com.lp.razorpay_clone.payment.entity;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "payment")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Payment {

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

    @Column(nullable = false, length = 100)
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

    @Column(length = 100)
    String bankReference;

    @Column(length = 100)
    String errorCode;

    @Column(length = 100)
    String errorDescription;

    LocalDateTime authorizedAt;
    LocalDateTime capturedAt;
    LocalDateTime settledAt;
    LocalDateTime failedAt;
    LocalDateTime refundedAt;
}
