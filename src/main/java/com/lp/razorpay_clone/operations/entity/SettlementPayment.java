package com.lp.razorpay_clone.operations.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "settlement_payment")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SettlementPayment {

    @EmbeddedId
    SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    Settlement settlement;
}
