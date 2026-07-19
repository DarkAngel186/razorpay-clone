package com.lp.razorpay_clone.payment.processor;

import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor processor = paymentProcessors.get(request.paymentMethod());
        if (processor == null) {
            throw new IllegalArgumentException("No Processor found for payment method: " + request.paymentMethod());
        }
        return processor.charge(request);
    }
}
