package com.lp.razorpay_clone.payment.controller;

import com.lp.razorpay_clone.merchant.security.MerchantContext;
import com.lp.razorpay_clone.payment.dto.request.PaymentInitRequest;
import com.lp.razorpay_clone.payment.dto.response.PaymentResponse;
import com.lp.razorpay_clone.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestBody @Valid PaymentInitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(merchantContext.getMerchantId(), request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> initiatePayment(@PathVariable UUID paymentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.capture(merchantContext.getMerchantId(), paymentId));
    }
}
