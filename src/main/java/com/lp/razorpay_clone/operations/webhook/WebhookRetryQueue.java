package com.lp.razorpay_clone.operations.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebhookRetryQueue {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.webhook.delivery.redis-key:webhook-retry}")
    private String redisKey;

    public void enqueue(UUID webhookEventId, LocalDateTime retryAt) {
        redisTemplate
                .opsForZSet()
                .add(redisKey, webhookEventId.toString(), getEpochMilli(retryAt));
    }

    public void enqueueIfAbsent(UUID webhookEventId, LocalDateTime retryAt) {
        redisTemplate
                .opsForZSet()
                .addIfAbsent(redisKey, webhookEventId.toString(), getEpochMilli(retryAt));
    }

    public Set<UUID> pollDue(int limit) {
        long now = getEpochMilli(LocalDateTime.now());
        Set<ZSetOperations.TypedTuple<String>> dueEvents = redisTemplate
                .opsForZSet().rangeByScoreWithScores(redisKey, 0, now, 0, limit);

        if (dueEvents == null || dueEvents.isEmpty()) return Set.of();

        dueEvents.forEach(dueEvent -> redisTemplate
                .opsForZSet()
                .remove(redisKey, dueEvent.getValue()));

        return dueEvents.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    private static long getEpochMilli(LocalDateTime retryAt) {
        return retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
