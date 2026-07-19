package com.lp.razorpay_clone.payment.config;

import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.payment.processor.PaymentProcessor;
import com.lp.razorpay_clone.payment.processor.strategy.CardProcessor;
import com.lp.razorpay_clone.payment.processor.strategy.NetBankingProcessor;
import com.lp.razorpay_clone.payment.processor.strategy.UpiProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardProcessor cardProcessor;
    private final NetBankingProcessor netBankingProcessor;
    private final UpiProcessor upiProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, cardProcessor,
                PaymentMethod.NET_BANKING, netBankingProcessor,
                PaymentMethod.UPI, upiProcessor
        );
    }
}
