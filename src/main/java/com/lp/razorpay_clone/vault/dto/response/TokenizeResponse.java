package com.lp.razorpay_clone.vault.dto.response;

import com.lp.razorpay_clone.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand cardBrand,
        Integer expiryMonth,
        Integer expiryYear
) {}
