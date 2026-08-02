package com.lp.razorpay_clone.merchant.service.impl;

import com.lp.razorpay_clone.common.dto.response.WebhookTarget;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.merchant.api.MerchantWebhookApi;
import com.lp.razorpay_clone.merchant.dto.request.UpdateWebhookConfigRequest;
import com.lp.razorpay_clone.merchant.dto.response.WebhookConfigResponse;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import com.lp.razorpay_clone.merchant.entity.MerchantWebhookConfig;
import com.lp.razorpay_clone.merchant.mapper.WebhookConfigMapper;
import com.lp.razorpay_clone.merchant.repository.MerchantRepository;
import com.lp.razorpay_clone.merchant.repository.MerchantWebhookConfigRepository;
import com.lp.razorpay_clone.merchant.service.WebhookConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookConfigServiceImpl implements WebhookConfigService, MerchantWebhookApi {

    private final MerchantWebhookConfigRepository merchantWebhookConfigRepository;
    private final MerchantRepository merchantRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Transactional
    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest updateWebhookConfigRequest) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MERCHANT", "Merchant with id: " + merchantId + " not found!!"));

        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes(StandardCharsets.UTF_8);

        String encryptedSecret = Base64.getEncoder().encodeToString(
                bytesEncryptor.encrypt(rawSecretBytes)
        );

        MerchantWebhookConfig config = MerchantWebhookConfig.builder()
                .merchant(merchant)
                .targetUrl(updateWebhookConfigRequest.targetUrl())
                .enabled(true)
                .eventTypes(updateWebhookConfigRequest.eventTypes())
                .webhookSecret(encryptedSecret)
                .build();

        config = merchantWebhookConfigRepository.save(config);

        return webhookConfigMapper.toResponse(config, rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> getAll(UUID merchantId) {
        return getAllActiveConfigsForMerchant(merchantId)
                .stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID webhookConfigId) {
        return webhookConfigMapper.toResponse(
                getConfigById(merchantId, webhookConfigId), null
        );
    }

    @Transactional
    @Override
    public WebhookConfigResponse update(UUID merchantId, UUID webhookConfigId, UpdateWebhookConfigRequest updateWebhookConfigRequest) {
        MerchantWebhookConfig config = getConfigById(merchantId, webhookConfigId);

        config.setTargetUrl(updateWebhookConfigRequest.targetUrl());
        config.setEventTypes(updateWebhookConfigRequest.eventTypes());

        return webhookConfigMapper.toResponse(config, null);
    }

    @Transactional
    @Override
    public void delete(UUID merchantId, UUID webhookConfigId) {
          MerchantWebhookConfig config = getConfigById(merchantId, webhookConfigId);
          config.setEnabled(false);
    }

    @Override
    public List<WebhookTarget> getActiveWebhookTargetForMerchant(UUID merchantId, String eventTypes) {
        return getAllActiveConfigsForMerchant(merchantId)
                .stream()
                .filter(config -> config.isSubscribedTo(eventTypes))
                .map(config -> new WebhookTarget(
                        config.getTargetUrl(),
                        config.getEventTypes(),
                        new String(bytesEncryptor.decrypt(config.getWebhookSecret().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8)
                ))
                .toList();
    }


    // Private Methods:
    private MerchantWebhookConfig getConfigById(UUID merchantId, UUID webhookConfigId) {
        return merchantWebhookConfigRepository.findByIdAndMerchant_Id(webhookConfigId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("WEBHOOK_CONFIG", "Webhook config with id: " + webhookConfigId + " not found for merchant with id: " + merchantId));
    }

    private List<MerchantWebhookConfig> getAllActiveConfigsForMerchant(UUID merchantId) {
        return merchantWebhookConfigRepository
                .findByMerchant_IdAndEnabledTrue(merchantId);
    }
}
