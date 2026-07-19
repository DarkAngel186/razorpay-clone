package com.lp.razorpay_clone.payment.gateway.dto;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.PaymentMethod;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PaymentRequest(

        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {
}
