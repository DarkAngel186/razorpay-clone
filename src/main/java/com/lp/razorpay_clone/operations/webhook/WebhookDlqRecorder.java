package com.lp.razorpay_clone.operations.webhook;

import com.lp.razorpay_clone.common.enums.WebhookEventStatus;
import com.lp.razorpay_clone.operations.entity.DlqEvent;
import com.lp.razorpay_clone.operations.entity.WebhookEvent;
import com.lp.razorpay_clone.operations.repository.DlqEventRepository;
import com.lp.razorpay_clone.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDlqRecorder {

    private final WebhookEventRepository webhookEventRepository;
    private final DlqEventRepository dlqEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAfterAttemptsExhausted(WebhookEvent webhookEvent, String finalError) {
        webhookEvent.setStatus(WebhookEventStatus.DEAD);
        webhookEventRepository.save(webhookEvent);

        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(webhookEvent)
                .merchantId(webhookEvent.getMerchantId())
                .finalError(finalError)
                .payload(webhookEvent.getPayload())
                .build();

        dlqEventRepository.save(dlqEvent);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordConsumerFailed(ConsumerRecord<String, Map<String, Object>> record, String finalError) {
        Map<String, Object> envelope = record.value();

        UUID merchantId = null;

        try {
            Map<String, Object> payload = (Map<String, Object>) envelope.get("data");
            Object merchantIdObj = payload != null ? payload.get("merchantId") : null;
            if (merchantIdObj != null) {
                merchantId = UUID.fromString(merchantIdObj.toString());
            }
        } catch (Exception ignored) {

        }

        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(null)
                .merchantId(merchantId)
                .finalError(finalError)
                .payload(envelope)
                .build();

        dlqEventRepository.save(dlqEvent);
    }
}
