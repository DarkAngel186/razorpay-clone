package com.lp.razorpay_clone.payment.outbox;

import com.lp.razorpay_clone.common.config.KafkaProperties;
import com.lp.razorpay_clone.common.enums.OutboxStatus;
import com.lp.razorpay_clone.payment.entity.OutboxEvent;
import com.lp.razorpay_clone.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for(OutboxEvent outboxEvent : pendingEvents) {
            try {
                String topic = kafkaProperties.topicFor(outboxEvent.getEventAggregateType());
                String key = extractMerchantId(outboxEvent.getPayload());

                Map<String, Object> envelope = Map.of(
                        "eventType", outboxEvent.getEventType(),
                        "aggregateType", outboxEvent.getEventAggregateType().name(),
                        "aggregateId", outboxEvent.getAggregateId(),
                        "data", outboxEvent.getPayload()
                );

                kafkaTemplate.send(topic, key, envelope)
                        .get(5, TimeUnit.SECONDS);

                outboxResultHandler.handleEventPublished(outboxEvent);
            } catch (Exception e) {
                log.error("Error while sending event, eventId: {}, attempts: {}", outboxEvent.getId(), outboxEvent.getAttempts());
                outboxResultHandler.handleEventFailed(outboxEvent, e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object value = payload.get("merchant_id");
        return value != null ? value.toString() : "unknown";
    }
}
