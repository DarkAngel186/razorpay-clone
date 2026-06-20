package com.lp.razorpay_clone.operations.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettlementPaymentId {

    UUID settlementId;
    UUID paymentId;
}
