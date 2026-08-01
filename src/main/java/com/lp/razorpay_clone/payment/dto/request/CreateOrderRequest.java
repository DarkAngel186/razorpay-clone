package com.lp.razorpay_clone.payment.dto.request;

import com.lp.razorpay_clone.common.entity.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(

        @NotNull(message = "Amount is required")
        Money amount,

        @Size(max = 100, message = "Receipt id must not be greater than 100 characters.")
        String receipt, //order-id known to merchant

        Map<String, Object> notes,

        LocalDateTime expiresAt,

        @Valid
        CustomerDetails customer
) {
        public record CustomerDetails(

                @Size(max = 100, message = "Customer Name should not be greater than 100 words")
                String name,

                @Size(max = 100, message = "Customer Email should not be greater than 100 words")
                String email,

                @Size(max = 20, message = "Customer Phone should not be greater than 100 words")
                String phone
        ) {}
}
