package com.lp.razorpay_clone.operations.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
import com.lp.razorpay_clone.common.enums.WebhookEventStatus;
import com.lp.razorpay_clone.common.enums.WebhookEventsType;
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
@Table(name = "webhook_event")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class WebhookEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    UUID merchantId;

    @Column(nullable = false, length = 100)
    String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    Map<String, Object> payload;

    @Column(nullable = false, length = 200)
    String targetUrl;

    @Column(nullable = false)
    String signature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    WebhookEventStatus status;

    @Column(nullable = false)
    Integer attempts = 0;

    Integer lastResponseCode;

    @Column(length = 1000)
    String lastResponseBody;

    LocalDateTime nextRetryAt;
    LocalDateTime lastAttemptedAt;
    LocalDateTime deliveredAt;
}
