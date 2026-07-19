package com.lp.razorpay_clone.payment.simulator;

import com.lp.razorpay_clone.common.enums.ChaosMode;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.payment.entity.Payment;
import com.lp.razorpay_clone.payment.repository.PaymentRepository;
import com.lp.razorpay_clone.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval:5000}")
    public void processCallbacks() {
        List<Payment> candidates = paymentRepository.findByStatusAndCreatedAtBefore(
                        PaymentStatus.AUTHORIZING,
                        LocalDateTime.now().minusSeconds(1)
                );

        if(candidates ==  null || candidates.isEmpty()) return;;

        for(Payment payment : candidates) {
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment) {
        log.info("Processing payments for payment id: {}" ,payment.getId());
        SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig = simulatorConfig.configFor(payment.getPaymentMethod());
        LocalDateTime dueAt = dueAt(payment, methodSimulatorConfig);

        if(LocalDateTime.now().isBefore(dueAt)) return;

        ChaosMode mode = simulatorConfig.getChaosMode();

        switch (mode) {
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> {
                log.debug("Payment {} timed out", payment.getId());
            }
            case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodSimulatorConfig));
        }
    }

    private void resolve(Payment payment, Boolean approve) {
        if(approve) {
            String bankReference = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankReference, null , null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Declined!!");
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 101;
        return bucket < methodSimulatorConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {

        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW) delaySeconds *= 2;

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }
}
