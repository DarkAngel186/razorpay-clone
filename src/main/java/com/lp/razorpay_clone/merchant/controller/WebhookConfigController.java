package com.lp.razorpay_clone.merchant.controller;

import com.lp.razorpay_clone.merchant.dto.request.UpdateWebhookConfigRequest;
import com.lp.razorpay_clone.merchant.dto.response.WebhookConfigResponse;
import com.lp.razorpay_clone.merchant.security.MerchantContext;
import com.lp.razorpay_clone.merchant.service.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/webhooks")
@RequiredArgsConstructor
public class WebhookConfigController {

    private final WebhookConfigService webhookConfigService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<WebhookConfigResponse> create(@RequestBody @Valid UpdateWebhookConfigRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(webhookConfigService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping
    public ResponseEntity<List<WebhookConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok(webhookConfigService.getAll(merchantContext.getMerchantId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookConfigResponse> getConfigById(@PathVariable UUID id) {
        return ResponseEntity.ok(webhookConfigService.getById(merchantContext.getMerchantId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookConfigResponse> updateConfigById(@PathVariable UUID id,
                                                                  @RequestBody @Valid UpdateWebhookConfigRequest request) {
        return ResponseEntity.ok(webhookConfigService.update(merchantContext.getMerchantId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable UUID id) {
        webhookConfigService.delete(merchantContext.getMerchantId(), id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
