package com.lp.razorpay_clone.common.config;

import com.lp.razorpay_clone.common.enums.EventAggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {

    private Map<String, String> topics;

    public String topicFor(EventAggregateType eventAggregateType) {
        String topic = topics.get(eventAggregateType.name().toLowerCase());
        if(topic == null) {
            throw new IllegalStateException("No Kafka topic for " + eventAggregateType.name().toLowerCase());
        }
        return topic;
    }
}
