package com.lp.razorpay_clone.payment.repository;

import com.lp.razorpay_clone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}