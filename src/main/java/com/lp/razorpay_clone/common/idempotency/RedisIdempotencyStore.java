package com.lp.razorpay_clone.common.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisIdempotencyStore implements IdempotencyStore{

    private static final String PREFIX = "idempotency:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean setIfAbsent(String key, Duration ttl) {
        try {
            Boolean set =  redisTemplate.opsForValue().setIfAbsent(PREFIX + key, IN_PROGRESS, ttl);
            return Boolean.TRUE.equals(set);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return true;
        }
    }

    @Override
    public void store(String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(PREFIX + key, value, ttl);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + key));
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(PREFIX + key);
        }  catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
}
