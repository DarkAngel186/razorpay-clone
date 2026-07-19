package com.lp.razorpay_clone.operations.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettlementPaymentId {

    UUID settlementId;
    UUID paymentId;
}
