package com.lp.razorpay_clone.operations.entity;

import com.lp.razorpay_clone.common.entity.BaseEntity;
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
@Table(name = "dlq_event")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DlqEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    WebhookEvent webhookEvent;

    @Column(nullable = false)
    UUID merchantId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    Map<String, Object> payload;

    @Column(length = 1000)
    String finalError;

    LocalDateTime movedAt;
    LocalDateTime replayedAt;
}
