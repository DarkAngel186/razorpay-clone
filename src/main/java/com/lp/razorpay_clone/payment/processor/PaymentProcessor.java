package com.lp.razorpay_clone.payment.processor;

import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
