package com.lp.razorpay_clone.merchant.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final MerchantContext merchantContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("Authorization header missing");
                filterChain.doFilter(request, response);
            }

            String jwtToken = authHeader.split("Bearer ")[1];

            Claims claims = jwtUtil.verifyAcsessToken(jwtToken);

            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtUtil.extractRoles(claims)))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                merchantContext.setMerchantId(jwtUtil.extractMerchantId(claims));
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error during JWT authentication: {}", e.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
