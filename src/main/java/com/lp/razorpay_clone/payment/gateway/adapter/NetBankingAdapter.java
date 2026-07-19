package com.lp.razorpay_clone.payment.gateway.adapter;

import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.payment.gateway.PaymentAdapter;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentRequest;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentResult;
import com.lp.razorpay_clone.payment.processor.PaymentProcessorRouter;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NetBankingAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        log.info("Initiating payment with NetBanking Adapter with payment id: {}", paymentRequest.paymentId());

        try {
            PaymentProcessorRequest request = PaymentProcessorRequest.nonCard(
                    paymentRequest.paymentId(),
                    PaymentMethod.NET_BANKING,
                    paymentRequest.amount(),
                    paymentRequest.methodDetails()
            );

            PaymentProcessorResponse response = paymentProcessorRouter.charge(request);

            return switch (response) {
                case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(
                        failure.errorCode(),
                        failure.errorDescription()
                );

                case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(
                        pending.processorReference()
                );

                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(
                        success.bankReference()
                );
            };
        } catch (Exception e) {
            log.warn("Error while initiating payment with NetBanking Adapter with payment id: {}", paymentRequest.paymentId(), e);
            return new PaymentResult.Failure("NET_BANKING_FAILED", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("NET_BANKING_CAPTURED");
    }
}
