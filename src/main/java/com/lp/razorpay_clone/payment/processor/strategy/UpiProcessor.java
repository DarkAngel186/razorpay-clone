package com.lp.razorpay_clone.payment.processor.strategy;

import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.payment.processor.PaymentProcessor;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpiProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String VPA_CODE_FAIL = "fail@okaxis";

        String vpa = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

        if(vpa != null && vpa.equals(VPA_CODE_FAIL)) {
            return new PaymentProcessorResponse.Failure(
                    "UPI_FAILED",
                    "Bank rejected the payment"
            );
        }

        String processorReference = "UPI_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        String bankReference = "BANK_REF_" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Success(processorReference, bankReference);
    }
}
