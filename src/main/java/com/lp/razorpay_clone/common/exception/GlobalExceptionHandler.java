package com.lp.razorpay_clone.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        ex.getResourceName().toUpperCase()+"_NOT_FOUND",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Remaining", "0")
                .header("X-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
                .header("X-Rate-Limit-Reset", Instant.now().plusSeconds(ex.getRetryAfterSeconds()).toString())
                .body(ErrorResponse.of(
                        "RATE_LIMIT_EXCEEDED",
                        ex.getMessage()
                        )
                );
    }
}
