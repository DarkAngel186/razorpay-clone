package com.lp.razorpay_clone.payment.service;

import com.lp.razorpay_clone.payment.dto.request.PaymentInitRequest;
import com.lp.razorpay_clone.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);

    void resolveAuthorization(UUID paymentId, boolean approved, String bankReference, String errorCode, String errorDescription);
}
