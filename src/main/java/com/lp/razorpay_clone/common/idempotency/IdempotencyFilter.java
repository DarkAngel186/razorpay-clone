package com.lp.razorpay_clone.common.idempotency;

import com.lp.razorpay_clone.common.exception.IdempotencyConflictException;
import com.lp.razorpay_clone.merchant.security.MerchantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Set<String> GUARDED_METHODS = Set.of("POST", "PUT", "PATCH");
    private static final Duration IN_PROGRESS_TTL = Duration.ofSeconds(30);
    private static final Duration COMPLETED_TTL = Duration.ofHours(24);
    private static final String SEPERATOR = "|";

    private final MerchantContext merchantContext;
    private final IdempotencyStore idempotencyStore;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(!GUARDED_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String rawKey = request.getHeader("X-Idempotency-Key");
        if(rawKey == null || rawKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID merchantId = merchantContext.getMerchantId();
        String key = merchantId != null ? merchantId + ":" + rawKey : rawKey;

        boolean claimed = idempotencyStore.setIfAbsent(key, IN_PROGRESS_TTL);

        if(!claimed) {
            Optional<String> existing = idempotencyStore.get(key);
            if(existing.isPresent() && !IdempotencyStore.IN_PROGRESS.equals(existing.get())) {
                replayResponse(request, response, existing.get());
            } else {
                var ex = new IdempotencyConflictException("A Request with same Idempotency key is in Progress!!!!");
                handlerExceptionResolver.resolveException(request, response, null, ex);
            }
            return;
        }

        // first time claimed:
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, wrapper);
        } finally {
            int status = wrapper.getStatus();
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            if(status < 400 && !body.isEmpty()) {

                String stored = status + SEPERATOR + body;
                idempotencyStore.store(key, stored, COMPLETED_TTL);
            } else {
                idempotencyStore.delete(key);
            }

            wrapper.copyBodyToResponse();
        }

    }

    private void replayResponse(HttpServletRequest request, HttpServletResponse response, String s) throws IOException {
        int seperatorIndex = s.indexOf(SEPERATOR);
        if(seperatorIndex < 0) {
            var ex = new IdempotencyConflictException("A Request with same Idempotency key is in Progress!!!!");
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        int status = Integer.parseInt(s.substring(0, seperatorIndex));
        String body = s.substring(seperatorIndex + 1);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
