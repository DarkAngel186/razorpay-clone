package com.lp.razorpay_clone.common.exception;

public class IdempotencyConflictException extends RuntimeException{

    public IdempotencyConflictException(String message) {
        super(message);
    }
}
