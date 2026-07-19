package com.lp.razorpay_clone.vault.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpiryYearValidator.class)
public @interface ExpiryYear {

    String message() default "Invalid expiry year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
