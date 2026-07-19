package com.lp.razorpay_clone.payment.statemachine;

import com.lp.razorpay_clone.common.enums.PaymentActor;
import com.lp.razorpay_clone.common.enums.PaymentEvent;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import com.lp.razorpay_clone.payment.entity.Payment;
import com.lp.razorpay_clone.payment.entity.PaymentTransitionLog;
import com.lp.razorpay_clone.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .toStatus(next)
                .paymentEvent(event)
                .actor(PaymentActor.SYSTEM)
                .occurredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);
        return next;
    }
}
