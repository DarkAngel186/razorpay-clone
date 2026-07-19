package com.lp.razorpay_clone.vault.service;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import com.lp.razorpay_clone.vault.dto.request.TokenizeRequest;
import com.lp.razorpay_clone.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}