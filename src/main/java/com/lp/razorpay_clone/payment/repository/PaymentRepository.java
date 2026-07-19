package com.lp.razorpay_clone.payment.repository;

import com.lp.razorpay_clone.common.enums.PaymentStatus;
import com.lp.razorpay_clone.payment.entity.OrderRecord;
import com.lp.razorpay_clone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByOrder_Id(OrderRecord orderRecord);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime createdAt);
}