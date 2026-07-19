package com.lp.razorpay_clone.payment.processor.strategy;

import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.payment.processor.PaymentProcessor;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(pan.equals(PAN_CARD_DECLINED)) {
            log.warn("Card declined for paymentId: {}", request.paymentId());
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "The card was declined by the bank.");
        }
        if(pan.equals(PAN_CARD_EXPIRED)) {
            log.warn("Card expired for paymentId: {}", request.paymentId());
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "The card has expired.");
        }

        String processorReference = "CARD_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
