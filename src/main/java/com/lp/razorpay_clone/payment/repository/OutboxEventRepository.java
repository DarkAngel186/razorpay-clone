package com.lp.razorpay_clone.payment.repository;

import com.lp.razorpay_clone.common.enums.OutboxStatus;
import com.lp.razorpay_clone.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}