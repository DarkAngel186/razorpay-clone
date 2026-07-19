package com.lp.razorpay_clone.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
public class FixedWindowRateLimiter implements RateLimiter{

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestsAllowed, long windowSeconds) {

        String redisKey = "ratelimit:fixed:" + key;
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);

        if(count == null) return RateLimitResult.allowed(maxRequestsAllowed);

        if(count == 1) {
            stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        if(count > maxRequestsAllowed) {
            Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS );
            int retryAfterSeconds = ttl != null && ttl > 0 ? ttl.intValue() : (int) windowSeconds;
            return RateLimitResult.denied(retryAfterSeconds);
        }

        return RateLimitResult.allowed(maxRequestsAllowed - count.intValue());
    }
}
