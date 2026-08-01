package com.lp.razorpay_clone.payment.outbox;

import com.lp.razorpay_clone.common.enums.EventAggregateType;
import com.lp.razorpay_clone.payment.entity.OutboxEvent;
import com.lp.razorpay_clone.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType eventAggregateType, UUID aggregateId, String eventType,
                        Map<String, Object> payload) {

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .eventAggregateType(eventAggregateType)
                .payload(payload)
                .build();

        outboxEventRepository.save(outboxEvent);
    }
}
