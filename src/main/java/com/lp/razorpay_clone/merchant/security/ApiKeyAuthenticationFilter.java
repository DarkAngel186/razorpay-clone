package com.lp.razorpay_clone.merchant.security;

import com.lp.razorpay_clone.common.exception.RateLimitException;
import com.lp.razorpay_clone.common.ratelimit.RateLimitResult;
import com.lp.razorpay_clone.common.ratelimit.RateLimiter;
import com.lp.razorpay_clone.merchant.cache.ApiKeyCache;
import com.lp.razorpay_clone.merchant.cache.ApiKeyCacheEntry;
import com.lp.razorpay_clone.merchant.entity.ApiKey;
import com.lp.razorpay_clone.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC_PREFIX = "Basic ";
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:60}")
    private Integer maxRequestsPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX)) {
                filterChain.doFilter(request, response);
            }

            String[] credentials = decode(authHeader);
            if (credentials == null) {
                throw new BadCredentialsException("Bad credentials");
            }

            String keyId = credentials[0];
            String rawSecret = credentials[1];

            ApiKeyCacheEntry apiKeyCacheEntry = apiKeyCache.get(keyId)
                    .orElseGet(() -> loadAndCache(keyId));

            if (apiKeyCacheEntry == null || !apiKeyCacheEntry.isEnabled() || !secretMatchers(rawSecret, apiKeyCacheEntry)) {
                throw new BadCredentialsException("Invalid API Key");
            }

            RateLimitResult result = rateLimiter.check(
                    "apiKey:" + keyId,
                    maxRequestsPerMinute, // max requests allowed
                    60 // window in seconds
            );

            if(!result.allowed()) {
                log.warn("Too many requests for API Key: " + keyId);
                throw new RateLimitException("Too Many Requests", result.retryAfterSeconds());
            }

            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));

            var auth = new UsernamePasswordAuthenticationToken(
                    keyId,
                    null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setKeyId(apiKeyCacheEntry.keyId());
            merchantContext.setMerchantId(apiKeyCacheEntry.merchantId());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error during API Key authentication: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {

        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElse(null);
        if(apiKey == null) return null;

        ApiKeyCacheEntry entry = new ApiKeyCacheEntry(
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getPreviousKeySecretHash(),
                apiKey.getGracePeriodExpiresAt(),
                apiKey.getMerchant().getId(),
                apiKey.getEnvironment(),
                apiKey.getEnabled());

        apiKeyCache.put(keyId, entry);
        return entry;
    }

    private boolean secretMatchers(String rawSecret, ApiKeyCacheEntry apiKey) {
        if(BCRYPT.matches(rawSecret, apiKey.keySecretHash())) {
            return true;
        }

        return apiKey.isInGracePeriod()
                && apiKey.previousKeySecretHash() != null
                && BCRYPT.matches(rawSecret, apiKey.previousKeySecretHash());
    }

    private String[] decode(String header) {
        String encoded = header.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if(colon < 1) return null;

        return new String[]{decoded.substring(0, colon), decoded.substring(colon + 1)};
    }
}
