package com.lp.razorpay_clone.merchant.api;

import com.lp.razorpay_clone.common.dto.response.WebhookTarget;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookApi {

    List<WebhookTarget> getActiveWebhookTargetForMerchant(UUID merchantId, String eventTypes);
}
