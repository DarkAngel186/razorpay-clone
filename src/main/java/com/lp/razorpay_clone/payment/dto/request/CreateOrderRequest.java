package com.lp.razorpay_clone.payment.dto.request;

import com.lp.razorpay_clone.common.entity.Money;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(

        @NotNull(message = "Amount is required")
        Money amount,
        String receipt, //order-id known to merchant
        Map<String, Object> notes,
        LocalDateTime expiresAt
) {
}
