package com.lp.razorpay_clone.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Money {

    private int amountUnits;
    private String currency;

    public static Money of(int amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public static Money inr(int amountUnits) {
        return new Money(amountUnits, "INR");
    }

    public Money add(Money money) {
        if(!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currency");
        }
        return new Money(this.amountUnits + money.amountUnits, money.currency);
    }

    public Money subtract(Money money) {
        if(!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Cannot subtract money with different currency");
        }
        return new Money(this.amountUnits - money.amountUnits, money.currency);
    }
}
