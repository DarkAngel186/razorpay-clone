package com.lp.razorpay_clone.merchant.service;

import com.lp.razorpay_clone.merchant.dto.response.MerchantResponse;
import com.lp.razorpay_clone.merchant.dto.request.MerchantSignupRequest;

public interface AuthService {

    MerchantResponse signup(MerchantSignupRequest request);
}
