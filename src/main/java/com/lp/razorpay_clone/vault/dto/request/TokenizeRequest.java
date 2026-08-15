package com.lp.razorpay_clone.vault.dto.request;

import com.lp.razorpay_clone.vault.validation.ExpiryYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(

        @NotBlank(message = "PAN cannot be blank")
        @LuhnCheck(message = "Invalid Card Number!")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "PAN must be between 13 to 19 digits")
        String pan,

        @NotBlank(message = "CVV cannot be blank")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
        String cvv,

        @NotNull(message = "Expiry Month cannot be blank")
        @Min(value = 1, message = "Expiry Month must be >= 1")
        @Max(value = 12, message = "Expiry Month must be <= 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry Year cannot be blank")
        @ExpiryYear
        Integer expiryYear,

        @NotNull(message = "Customer ID cannot be blank")
        UUID customerId,

        @Size(min = 2, max = 100, message = "Card Holder Name cannot be blank")
        String cardHolderName
) {}
