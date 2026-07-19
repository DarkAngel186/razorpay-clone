package com.lp.razorpay_clone.payment.gateway;

import com.lp.razorpay_clone.payment.gateway.dto.PaymentRequest;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest paymentRequest);

    PaymentResult capture(UUID paymentId);
}
