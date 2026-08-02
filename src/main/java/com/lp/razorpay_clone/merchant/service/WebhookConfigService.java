package com.lp.razorpay_clone.merchant.service;

import com.lp.razorpay_clone.merchant.dto.request.UpdateWebhookConfigRequest;
import com.lp.razorpay_clone.merchant.dto.response.WebhookConfigResponse;

import java.util.List;
import java.util.UUID;

public interface WebhookConfigService {

    WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest updateWebhookConfigRequest);

    List<WebhookConfigResponse> getAll(UUID merchantId);

    WebhookConfigResponse getById(UUID merchantId, UUID webhookConfigId);

    WebhookConfigResponse update(UUID merchantId, UUID webhookConfigId, UpdateWebhookConfigRequest updateWebhookConfigRequest);

    void delete(UUID merchantId, UUID webhookConfigId);
}
