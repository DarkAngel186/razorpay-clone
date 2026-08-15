package com.lp.razorpay_clone.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(

        @NotBlank(message = "Webhook URL cannot be blank")
        @Size(max = 500, message = "Webhook URL cannot exceed 500 characters")
        @Pattern(
                regexp = "^http?://.+",
                message = "Webhook URL must be a valid http(s) URL"
        )
        String targetUrl,

        // Comma-separated events : "PAYMENT_SUCCESS, PAYMENT_FAILED, REFUND_INITIATED"
        // Null/Blank means ALL
        @Size(max = 1000)
        String eventTypes
) {
}
