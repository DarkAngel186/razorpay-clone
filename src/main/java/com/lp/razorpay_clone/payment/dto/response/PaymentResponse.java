package com.lp.razorpay_clone.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails,
        String bankReference,
        String errorCode,
        String errorDescription,
        LocalDateTime authorizedAt,
        LocalDateTime capturedAt,
        LocalDateTime settledAt,
        LocalDateTime failedAt,
        LocalDateTime refundedAt
) { }
