package com.lp.razorpay_clone.merchant.repository;

import com.lp.razorpay_clone.merchant.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

}
