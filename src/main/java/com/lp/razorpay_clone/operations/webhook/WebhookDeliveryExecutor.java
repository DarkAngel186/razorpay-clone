package com.lp.razorpay_clone.operations.webhook;

import com.lp.razorpay_clone.common.enums.WebhookEventStatus;
import com.lp.razorpay_clone.operations.entity.WebhookEvent;
import com.lp.razorpay_clone.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final RestClient restClient;
    private final WebhookDlqRecorder webhookDlqRecorder;

    private static final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(30),
            Duration.ofHours(2),
            Duration.ofHours(8),
            Duration.ofHours(24)
    );

    private final int MAX_ATTEMPTS = 7;

    @Value("${webhook.delivery.signature:X-Razorpay-Signature}")
    private String signatureHeader;

    @Transactional
    public void deliver(UUID webhookEventId) {
        Optional<WebhookEvent> webhookEvent = webhookEventRepository.findById(webhookEventId);
        if(webhookEvent.isEmpty()) {
            log.warn("Webhook event with id {} not found", webhookEventId);
            return;
        }

        WebhookEvent event = webhookEvent.get();
        if(event.getStatus() == WebhookEventStatus.DELIVERED || event.getStatus() == WebhookEventStatus.FAILED) {
            log.warn("Webhook event with id {} has already been processed or failed, status: {}", webhookEventId , event.getStatus());
            return;
        }

        event.setAttempts(event.getAttempts() + 1);
        event.setLastAttemptedAt(LocalDateTime.now());

        try {
            var response = restClient.post()
                    .uri(event.getTargetUrl())
                    .header(signatureHeader, event.getSignature())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "event", event.getEventType(),
                            "payload", event.getPayload()
                    ))
                    .retrieve()
                    .toBodilessEntity();

            int statusCode = response.getStatusCode().value();
            event.setLastResponseCode(statusCode);

            if(response.getStatusCode().is2xxSuccessful()) {
                event.setStatus(WebhookEventStatus.DELIVERED);
                event.setDeliveredAt(LocalDateTime.now());
                webhookEventRepository.save(event);
                log.info("Successfully delivered webhook event with id {}", webhookEventId);
                return;
            }

            handleAttemptFailed(event, "HTTP"+statusCode);
        } catch (RestClientException e) {
            event.setLastResponseBody(e.getMessage());
            handleAttemptFailed(event, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleAttemptFailed(WebhookEvent event, String error) {
        event.setLastResponseBody(error);

        if(event.getAttempts() >= MAX_ATTEMPTS) {
            event.setStatus(WebhookEventStatus.DEAD);
            webhookDlqRecorder.recordAfterAttemptsExhausted(event, error);
            log.error("Webhook event with id {} has died after {} attempts", event.getId(), event.getAttempts());
            return;
        }

        Duration backoff = BACKOFF.get(event.getAttempts() - 1);
        LocalDateTime nextRetry = LocalDateTime.now().plus(backoff);
        event.setStatus(WebhookEventStatus.FAILED);
        event.setNextRetryAt(nextRetry);

        webhookEventRepository.save(event);

        webhookRetryQueue.enqueue(event.getId(), nextRetry);
        log.info("Scheduled retry for webhook event with id {} at {}", event.getId(), nextRetry);
    }
}
