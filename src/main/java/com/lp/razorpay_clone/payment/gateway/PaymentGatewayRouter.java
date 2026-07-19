package com.lp.razorpay_clone.payment.gateway;

import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentRequest;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest paymentRequest) {
        PaymentAdapter adapter = paymentAdapters.get(paymentRequest.paymentMethod());
        if(adapter == null) {
            throw new IllegalArgumentException("No Adapter found for payment method: " + paymentRequest.paymentMethod());
        }
        return adapter.initiate(paymentRequest);
    }

    public PaymentResult capture(PaymentMethod paymentMethod, UUID paymentId) {
        PaymentAdapter adapter = paymentAdapters.get(paymentMethod);
        if(adapter == null) {
            throw new IllegalArgumentException("No Adapter found for payment method: " + paymentMethod);
        }
        return adapter.capture(paymentId);
    }
}
