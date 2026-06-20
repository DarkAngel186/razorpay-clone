package com.lp.razorpay_clone.merchant.controller;

import com.lp.razorpay_clone.merchant.dto.request.ApiKeyRequest;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyResponse;
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
@RequestMapping("/v1/merchants/{merchantId}/api-keys")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyController {

    ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@PathVariable UUID merchantId,
                                                             @Valid @RequestBody ApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                apiKeyService.createApiKey(merchantId, request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> getAllApiKeys(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(apiKeyService.getAllApiKeys(merchantId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteApiKey(@PathVariable UUID merchantId, @RequestParam UUID keyId) {
        apiKeyService.deleteApiKey(merchantId, keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateApiKey(@PathVariable UUID merchantId,
                                                             @PathVariable UUID keyId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                apiKeyService.rotateApiKey(merchantId, keyId)
        );
    }
}
