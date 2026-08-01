package com.lp.razorpay_clone.payment.service.impl;

import com.lp.razorpay_clone.common.enums.EventAggregateType;
import com.lp.razorpay_clone.common.enums.OrderStatus;
import com.lp.razorpay_clone.common.enums.PaymentEvent;
import com.lp.razorpay_clone.common.enums.PaymentStatus;
import com.lp.razorpay_clone.common.exception.BusinessRuleViolationException;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.payment.dto.request.PaymentInitRequest;
import com.lp.razorpay_clone.payment.dto.response.PaymentResponse;
import com.lp.razorpay_clone.payment.entity.OrderRecord;
import com.lp.razorpay_clone.payment.entity.Payment;
import com.lp.razorpay_clone.payment.gateway.PaymentGatewayRouter;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentRequest;
import com.lp.razorpay_clone.payment.gateway.dto.PaymentResult;
import com.lp.razorpay_clone.payment.mapper.PaymentMapper;
import com.lp.razorpay_clone.payment.outbox.OutboxEventPublisher;
import com.lp.razorpay_clone.payment.repository.OrderRecordRepository;
import com.lp.razorpay_clone.payment.repository.PaymentRepository;
import com.lp.razorpay_clone.payment.service.PaymentService;
import com.lp.razorpay_clone.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRecordRepository orderRecordRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    @Transactional
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request) {

        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantIdForUpdate(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order with id: " + request.orderId() + " not found!!"));

        if(orderRecord.getStatus() != OrderStatus.CREATED && orderRecord.getStatus() != OrderStatus.ATTEMPTED) {
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE", "Order with status:" + orderRecord.getStatus() + " is not acceptable!");
        }

        orderRecord.setStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(orderRecord)
                .merchantId(merchantId)
                .amount(orderRecord.getAmount())
                .status(PaymentStatus.CREATED)
                .idempotencyKey(UUID.randomUUID().toString())
                .paymentMethod(request.paymentMethod())
                .methodDetails(request.methodDetails())
                .build();

         payment = paymentRepository.save(payment);

         PaymentRequest paymentRequest = new PaymentRequest(
                 payment.getId(),
                 orderRecord.getId(),
                 merchantId,
                 orderRecord.getAmount(),
                 request.paymentMethod(),
                 request.methodDetails()
         );

         paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
         PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

         switch (result) {
             case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationReference());
             case PaymentResult.Failure failure -> {
                 paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                 payment.setErrorCode(failure.errorCode());
                 payment.setErrorDescription(failure.errorDescription());
             }
             case PaymentResult.Success success -> {

             }
         }

         payment = paymentRepository.save(payment);
         orderRecordRepository.save(orderRecord);

        outboxEventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(), "PAYMENT_STATUS_CHANGED",
                Map.of("orderId", payment.getOrder().getId().toString(),
                        "paymentId", payment.getId().toString(),
                        "merchantId", merchantId.toString(),
                        "paymentStatus", payment.getStatus().name(),
                        "amountUnits", payment.getAmount().getAmountUnits(),
                        "amountCurrency", payment.getAmount().getCurrency(),
                        "paymentMethod", payment.getPaymentMethod().name()
                )
        );

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT", "Payment with id: " + paymentId + " not found!!"));

        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

         PaymentResult result = paymentGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

         if(result instanceof PaymentResult.Success success) {
             paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
             payment.setCapturedAt(LocalDateTime.now());
             log.info("Payment with id: {} captured successfully!}", paymentId);
         } else if(result instanceof PaymentResult.Failure(String errorCode, String errorDescription)) {
             paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
             payment.setErrorCode(errorCode);
             payment.setErrorDescription(errorDescription);
             log.warn("Payment with id: {} failed to capture!}", paymentId);
         }

         payment = paymentRepository.save(payment);

        outboxEventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(), "PAYMENT_STATUS_CHANGED",
                Map.of("orderId", payment.getOrder().getId().toString(),
                        "paymentId", payment.getId().toString(),
                        "merchantId", merchantId.toString(),
                        "paymentStatus", payment.getStatus().name(),
                        "amountUnits", payment.getAmount().getAmountUnits(),
                        "amountCurrency", payment.getAmount().getCurrency(),
                        "paymentMethod", payment.getPaymentMethod().name()
                )
        );

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approved, String bankReference, String errorCode, String errorDescription) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT", "Payment with id: " + paymentId + " not found!!"));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment with id: {} is not in AUTHORIZING state, cannot resolve authorization!, state:{}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approved) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankReference);
            payment.setAuthorizedAt(LocalDateTime.now());

            // Auto-Capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success(String reference)) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setBankReference(reference);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setStatus(OrderStatus.PAID);
                log.info("Payment with id: {} captured successfully!!}", paymentId);
            } else if(captureResult instanceof PaymentResult.Failure(String code, String description)) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(code);
                payment.setErrorDescription(description);
            }
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.warn("Payment with id: {} failed to authorize!}", paymentId);
        }

        paymentRepository.save(payment);
        orderRecordRepository.save(orderRecord);

        outboxEventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(), "PAYMENT_CREATED",
                Map.of("orderId", payment.getOrder().getId().toString(),
                        "paymentId", payment.getId().toString(),
                        "merchantId", payment.getMerchantId().toString(),
                        "paymentStatus", payment.getStatus().name(),
                        "amountUnits", payment.getAmount().getAmountUnits(),
                        "amountCurrency", payment.getAmount().getCurrency(),
                        "paymentMethod", payment.getPaymentMethod().name()
                )
        );
    }
}
