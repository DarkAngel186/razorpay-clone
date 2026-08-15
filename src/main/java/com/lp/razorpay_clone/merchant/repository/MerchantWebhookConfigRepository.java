package com.lp.razorpay_clone.merchant.repository;

import com.lp.razorpay_clone.merchant.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantWebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {

    List<MerchantWebhookConfig> findByMerchant_IdAndEnabledTrue(UUID merchantId);

    Optional<MerchantWebhookConfig> findByIdAndMerchant_Id(UUID webhookConfigId, UUID merchantId);
}