package com.lp.razorpay_clone.merchant.service;

import com.lp.razorpay_clone.merchant.dto.request.LoginRequest;
import com.lp.razorpay_clone.merchant.dto.response.LoginResponse;
import com.lp.razorpay_clone.merchant.dto.response.MerchantResponse;
import com.lp.razorpay_clone.merchant.dto.request.MerchantSignupRequest;
import jakarta.validation.Valid;

public interface AuthService {

    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(@Valid LoginRequest request);
}
