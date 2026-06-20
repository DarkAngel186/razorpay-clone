package com.lp.razorpay_clone.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponse(
        String errorCode,
        String message,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<FieldError> fieldErrors
) {

    record FieldError(
            String field,
            String message
    ) {}

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, Instant.now(), null);
    }

    public static ErrorResponse of(String errorCode, String message, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, message, Instant.now(), fieldErrors);
    }
}


