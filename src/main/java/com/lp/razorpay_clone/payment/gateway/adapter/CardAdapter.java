package com.lp.razorpay_clone.payment.gateway.adapter;

import com.lp.razorpay_clone.payment.gateway.PaymentAdapter;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentRequest;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentResult;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import com.lp.razorpay_clone.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {

        String token = (String) paymentRequest.methodDetails().get("token");

        PaymentProcessorResponse response = vaultService.charge(paymentRequest.paymentId(),
                token,
                paymentRequest.amount(),
                paymentRequest.methodDetails());

        return switch (response) {
            case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_CAPTURED");
    }
}
