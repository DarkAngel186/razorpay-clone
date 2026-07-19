package com.lp.razorpay_clone.common.exception;

public class InvalidStateException extends RuntimeException {

    String from;
    String to;

    public InvalidStateException(String from, String to) {
        super("Invalid state transition from " + from + " to " + to);
        this.from = from;
        this.to = to;
    }
}
