package com.lp.razorpay_clone.merchant.service;

import com.lp.razorpay_clone.merchant.dto.request.ApiKeyRequest;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse createApiKey(UUID merchantId, @Valid ApiKeyRequest request);

    List<ApiKeyResponse> getAllApiKeys(UUID merchantId);

    void deleteApiKey(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotateApiKey(UUID merchantId, UUID keyId);
}
