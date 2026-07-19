package com.lp.razorpay_clone.payment.dto.response;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {}
