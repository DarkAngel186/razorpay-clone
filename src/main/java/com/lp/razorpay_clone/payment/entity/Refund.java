package com.lp.razorpay_clone.payment.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "refund")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;

    @Column(nullable = false)
    UUID merchantId;

    @Embedded
    Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    RefundStatus status = RefundStatus.PENDING;

    @Column(length = 100)
    String bankReference;

    @Column(length = 100)
    String errorCode;

    @Column(length = 500)
    String errorDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Object> notes;

    LocalDateTime processedAt;
}
