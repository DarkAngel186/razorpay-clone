package com.lp.razorpay_clone.common.dto.response;

public record WebhookTarget(
        String targetUrl,
        String eventTypes,
        String webhookSecret
) {
}
