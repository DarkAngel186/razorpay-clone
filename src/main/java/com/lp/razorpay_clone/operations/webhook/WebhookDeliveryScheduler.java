package com.lp.razorpay_clone.operations.webhook;

import com.lp.razorpay_clone.common.enums.WebhookEventStatus;
import com.lp.razorpay_clone.operations.entity.WebhookEvent;
import com.lp.razorpay_clone.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryScheduler {

    private final WebhookRetryQueue webhookRetryQueue;
    private final WebhookEventRepository webhookEventRepository;

    @Value("${app.webhook.delivery.batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelay = 1000)
    public void pollAndDeliver() {
        Set<UUID> due = webhookRetryQueue.pollDue(batchSize);
        if (due.isEmpty()) {
            log.info("No due webhook events to deliver");
            return;
        }

        for (UUID webhookEventId : due) {
            // executor.deliver(webhookEventId);
        }
    }

    public void reconcileFromDatabase() {
        List<WebhookEvent> due = webhookEventRepository
                .findByStatusAndNextRetryAtBefore(WebhookEventStatus.PENDING, LocalDateTime.now());

        for (WebhookEvent webhookEvent : due) {
            webhookRetryQueue.enqueueIfAbsent(webhookEvent.getId(), webhookEvent.getNextRetryAt());
        }
    }
}
