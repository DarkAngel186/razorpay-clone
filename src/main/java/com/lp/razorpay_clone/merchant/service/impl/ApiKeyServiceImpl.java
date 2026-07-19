package com.lp.razorpay_clone.merchant.service.impl;

import com.lp.razorpay_clone.common.exception.BusinessRuleViolationException;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.merchant.cache.ApiKeyCache;
import com.lp.razorpay_clone.merchant.dto.request.ApiKeyRequest;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import com.lp.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import com.lp.razorpay_clone.merchant.entity.ApiKey;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import com.lp.razorpay_clone.merchant.mapper.ApiKeyMapper;
import com.lp.razorpay_clone.merchant.repository.ApiKeyRepository;
import com.lp.razorpay_clone.merchant.repository.MerchantRepository;
import com.lp.razorpay_clone.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    ApiKeyRepository apiKeyRepository;
    MerchantRepository merchantRepository;
    ApiKeyMapper apiKeyMapper;
    PasswordEncoder passwordEncoder;
    ApiKeyCache apiKeyCache;

    @Override
    @Transactional
    public ApiKeyCreateResponse createApiKey(UUID merchantId, ApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("MERCHANT", "Merchant with id: " + merchantId + " not found!!"));

        String keyId = "rzp_" + request.environment().name().toUpperCase()+ "_" + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40); //TODO: replace with cryptographic random hex

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .merchant(merchant)
                .keySecretHash(passwordEncoder.encode(rawSecret))
                .environment(request.environment())
                .build();

        apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), rawSecret, apiKey.getEnvironment());
    }

    @Override
    public List<ApiKeyResponse> getAllApiKeys(UUID merchantId) {
        return apiKeyMapper.toApiKeyResponseList(apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Override
    @Transactional
    public void deleteApiKey(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("API_KEY", "API Key with id: " + keyId + " not found for merchant with id: " + merchantId));

        apiKey.setEnabled(false);
        apiKeyCache.evict(apiKey.getKeyId());
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotateApiKey(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("API_KEY", "API Key with id: " + keyId + " not found for merchant with id: " + merchantId));

        if(!apiKey.getEnabled()) {
            throw new BusinessRuleViolationException("API_KEY", "Cannot rotate API Key because is not enabled!!!");
        }

        String newKeySecret = RandomizerUtil.randomBase64(40); //TODO: replace with cryptographic random hex

        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(passwordEncoder.encode(newKeySecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24)); //TODO: make grace period configurable

        apiKeyCache.evict(apiKey.getKeyId());

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), newKeySecret, apiKey.getEnvironment());
    }


}
