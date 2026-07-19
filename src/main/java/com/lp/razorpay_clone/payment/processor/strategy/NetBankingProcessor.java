package com.lp.razorpay_clone.payment.processor.strategy;

import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.payment.processor.PaymentProcessor;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NetBankingProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("bankCode").toString() : null;

        if(bankCode != null && bankCode.equals(BANK_CODE_FAIL)) {
            return new PaymentProcessorResponse.Failure(
                    "NET_BANKING_FAILED",
                    "Bank rejected the payment"
            );
        }

        String processorReference = "NB_PROCESSOR_" + RandomizerUtil.randomBase64(16);
//        String redirectReference = "https://REDIRECT_BANK.com/" + processorReference;

        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
