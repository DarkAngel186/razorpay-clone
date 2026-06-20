package com.lp.razorpay_clone.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        super("Resource: " +resourceName+ ", not found with name : " + identifier);
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
