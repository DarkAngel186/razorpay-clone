package com.lp.razorpay_clone.payment.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record",
        indexes = {
                @Index(name="idx_order_id_merchant_id", columnList = "id, merchant_id"),
                @Index(name="idx_order_merchant_id", columnList = "merchant_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID merchantId;

    @Embedded
    Money amount;

    @Column(length = 100)
    String receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    OrderStatus status = OrderStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    Integer attempts = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    Map<String, Object> notes;

    @Column(nullable = false)
    LocalDateTime expiresAt;
}
