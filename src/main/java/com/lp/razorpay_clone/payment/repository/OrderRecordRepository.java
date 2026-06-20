package com.lp.razorpay_clone.payment.repository;

import com.lp.razorpay_clone.payment.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRecordRepository extends JpaRepository<OrderRecord, UUID> {

    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);
}