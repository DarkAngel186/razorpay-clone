package com.lp.razorpay_clone.payment.processor.dto;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,
        UUID paymentId,
        Money amount,
        String pan,
        String expiry,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {

    public static PaymentProcessorRequest card(UUID paymentId, String pan, String expiry, Money amount, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(
                UUID.randomUUID(),
                paymentId,
                amount,
                pan,
                expiry,
                PaymentMethod.CARD,
                methodDetails
        );
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod paymentMethod, Money amount, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(
                UUID.randomUUID(),
                paymentId,
                amount,
                null,
                null,
                paymentMethod,
                methodDetails
        );
    }
}
