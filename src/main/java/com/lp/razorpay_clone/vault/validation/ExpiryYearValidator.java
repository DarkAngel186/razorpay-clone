package com.lp.razorpay_clone.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Annotation;
import java.time.Year;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, Integer> {

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        if(year == null){
            return false;
        }
        return year >= Year.now().getValue();
    }
}
