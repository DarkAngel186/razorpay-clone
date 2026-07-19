package com.lp.razorpay_clone.merchant.controller;

import com.lp.razorpay_clone.merchant.dto.request.ApiKeyRequest;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import com.lp.razorpay_clone.merchant.security.MerchantContext;
import com.lp.razorpay_clone.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/api-keys")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyController {

    ApiKeyService apiKeyService;
    MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@Valid @RequestBody ApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                apiKeyService.createApiKey(merchantContext.getMerchantId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getAllApiKeys() {
        return ResponseEntity.ok(apiKeyService.getAllApiKeys(merchantContext.getMerchantId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteApiKey( @RequestParam UUID keyId) {
        apiKeyService.deleteApiKey(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateApiKey(@PathVariable UUID keyId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                apiKeyService.rotateApiKey(merchantContext.getMerchantId(), keyId)
        );
    }
}
