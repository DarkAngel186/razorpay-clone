package com.lp.razorpay_clone.operations.webhook;

import com.lp.razorpay_clone.common.dto.response.WebhookTarget;
import com.lp.razorpay_clone.common.enums.WebhookEventStatus;
import com.lp.razorpay_clone.common.util.SignerUtil;
import com.lp.razorpay_clone.merchant.api.MerchantWebhookApi;
import com.lp.razorpay_clone.operations.entity.WebhookEvent;
import com.lp.razorpay_clone.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final WebhookEventRepository webhookEventRepository;
    private final MerchantWebhookApi merchantWebhookApi;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookRetryQueue webhookRetryQueue;

    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payments.events}",
            "${app.kafka.topics.order:orders.events}",
            "${app.kafka.topics.settlement:settlement.events}",
            "${app.kafka.topics.refund:refund.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String, Object>> record, Acknowledgment acknowledgment) {
        try {
            Map<String, Object> envelope = record.value();
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            String eventType = envelope.get("event_type").toString();

            Object merchantIdRaw = data.get("merchant_id");
            if (merchantIdRaw == null) {
                log.warn("merchant_id is null, skipping webhook event : {}", eventType);
                acknowledgment.acknowledge();
                return;
            }

            UUID merchantId = UUID.fromString(merchantIdRaw.toString());
            log.info("Received webhook event: {} for merchant: {}", eventType, merchantId);

            List<WebhookTarget> webhookTargets = merchantWebhookApi.getActiveWebhookTargetForMerchant(merchantId, eventType);

            if (webhookTargets == null || webhookTargets.isEmpty()) {
                log.warn("No webhook targets found for merchant: {} and event type: {}", merchantId, eventType);
                acknowledgment.acknowledge();
                return;
            }

            Map<String, Object> signatureData = Map.of(
                    "event", eventType,
                    "payload", data
            );
            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for (WebhookTarget webhookTarget : webhookTargets) {
                String signature = signerUtil.sign(signatureJson, webhookTarget.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .payload(data)
                        .signature(signature)
                        .targetUrl(webhookTarget.targetUrl())
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();

                webhookEventRepository.save(webhookEvent);

                // Enqueue in Redis:
                webhookRetryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
            }
            acknowledgment.acknowledge();  // Acknowledge to avoid reprocessing
        } catch (Exception e) {
            log.error("Error processing webhook event: {}, record offset: {}", e.getMessage(), record.offset(), e);
            // TODO: check exception for acknowledgment. If it's a recoverable error, we might want to not acknowledge and let it retry. For now, we acknowledge to avoid infinite loops.
        }
    }
}
