package com.lp.razorpay_clone.payment.statemachine;

import com.lp.razorpay_clone.common.enums.PaymentEvent;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import com.lp.razorpay_clone.common.exception.InvalidStateException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStateMachine {

    public record Transition(
            PaymentStatus from,
            PaymentEvent to
    ){}

    private static final Map<Transition, PaymentStatus> TRANSITION = Map.ofEntries(
            Map.entry(new Transition(PaymentStatus.CREATED, PaymentEvent.AUTHORIZE_ATTEMPT), PaymentStatus.AUTHORIZING),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_SUCCESS), PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_FAIL), PaymentStatus.FAILED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_REQUEST), PaymentStatus.CAPTURING),
            Map.entry(new Transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_SUCCESS), PaymentStatus.CAPTURED),
            Map.entry(new Transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_FAIL), PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.REFUND_INIT), PaymentStatus.PARTIALY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.PARTIALY_REFUNDED, PaymentEvent.REFUND_COMPLETE), PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.SETTLE), PaymentStatus.SETTLED),
            Map.entry(new Transition(PaymentStatus.SETTLED, PaymentEvent.REFUND_INIT), PaymentStatus.PARTIALY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED, PaymentEvent.REFUND_COMPLETE), PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CREATED, PaymentEvent.CANCEL), PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING, PaymentEvent.CANCEL), PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_TIMEOUT), PaymentStatus.AUTH_EXPIRED)
    );

    public PaymentStatus transition(PaymentStatus from, PaymentEvent to){
        PaymentStatus next = TRANSITION.get(new Transition(from, to));
        if(next == null){
            throw new InvalidStateException(from.name(), to.name());
        }
        return next;
    }
}
