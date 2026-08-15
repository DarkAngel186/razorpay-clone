package com.lp.razorpay_clone.operations.repository;

import com.lp.razorpay_clone.operations.entity.DlqEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {

}