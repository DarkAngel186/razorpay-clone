package com.lp.razorpay_clone.payment.config;

import com.lp.razorpay_clone.common.enums.PaymentMethod;
import com.lp.razorpay_clone.payment.gateway.PaymentAdapter;
import com.lp.razorpay_clone.payment.gateway.adapter.CardAdapter;
import com.lp.razorpay_clone.payment.gateway.adapter.NetBankingAdapter;
import com.lp.razorpay_clone.payment.gateway.adapter.UpiAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final UpiAdapter upiAdapter;
    private final CardAdapter cardAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardAdapter,
                PaymentMethod.NET_BANKING, netBankingAdapter,
                PaymentMethod.UPI, upiAdapter
        );
    }
}
