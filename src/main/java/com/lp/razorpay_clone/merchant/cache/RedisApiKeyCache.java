package com.lp.razorpay_clone.merchant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisApiKeyCache  implements ApiKeyCache{

    private static final String API_KEY_CACHE_PREFIX = "api_key:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(API_KEY_CACHE_PREFIX + keyId);
            if(json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
        } catch (Exception e) {
            log.error("Error retrieving API key from cache for keyId {}: {}", keyId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry entry) {
        try {
            stringRedisTemplate.opsForValue().set(API_KEY_CACHE_PREFIX + keyId,
                    objectMapper.writeValueAsString(entry),
                    TTL);
        } catch (Exception e) {
            log.error("Error storing API key in cache for keyId {}: {}", keyId, e.getMessage());
        }
    }

    @Override
    public void evict(String keyId) {
        try {
            stringRedisTemplate.delete(API_KEY_CACHE_PREFIX + keyId);
        } catch (Exception e) {
            log.error("Error evicting API key from cache for keyId {}: {}", keyId, e.getMessage());
        }
    }
}
