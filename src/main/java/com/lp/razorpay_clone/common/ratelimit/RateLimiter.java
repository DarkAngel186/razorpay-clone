package com.lp.razorpay_clone.common.ratelimit;

public interface RateLimiter {

    RateLimitResult check(String key, int maxRequestsAllowed, long windowSeconds);
}
