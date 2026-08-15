package com.lp.razorpay_clone.operations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WebhookClientConfig {

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
