package com.lp.razorpay_clone.payment.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.enums.PaymentActor;
import com.lp.razorpay_clone.common.enums.PaymentEvent;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_transition_log",
        indexes = {
                @Index(name="idx_payment_transition_log_payment_id", columnList = "payment_id")
        }
)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentTransitionLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentEvent paymentEvent;

    @Column(length = 100)
    PaymentActor actor;

    @Column(nullable = false)
    LocalDateTime occurredAt;
}
