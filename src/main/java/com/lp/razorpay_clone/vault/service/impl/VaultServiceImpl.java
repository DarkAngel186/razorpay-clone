package com.lp.razorpay_clone.vault.service.impl;

import com.lp.razorpay_clone.common.entity.Money;
import com.lp.razorpay_clone.common.enums.CardBrand;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.common.util.RandomizerUtil;
import com.lp.razorpay_clone.payment.processor.PaymentProcessorRouter;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import com.lp.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import com.lp.razorpay_clone.vault.config.VaultEncryptionConfig;
import com.lp.razorpay_clone.vault.dto.request.TokenizeRequest;
import com.lp.razorpay_clone.vault.dto.response.TokenizeResponse;
import com.lp.razorpay_clone.vault.entity.CardToken;
import com.lp.razorpay_clone.vault.entity.VaultCard;
import com.lp.razorpay_clone.vault.repository.CardTokenRepository;
import com.lp.razorpay_clone.vault.repository.VaultCardRepository;
import com.lp.razorpay_clone.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(request.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek).encrypt(request.pan().getBytes());
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard vaultCard = VaultCard.builder()
                .brand(cardBrand)
                .lastFour(lastFour)
                .bin(bin)
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .cardHolderName(request.cardHolderName())
                .expiryMonth(request.expiryMonth().toString())
                .expiryYear(request.expiryYear().toString())
                .build();

        vaultCardRepository.save(vaultCard);

        String token = "tok_" + RandomizerUtil.randomBase64(32);
        cardTokenRepository.save(CardToken.builder()
                .vaultCard(vaultCard)
                .token(token)
                .merchantId(merchantId)
                .customerId(request.customerId())
                .build());

        return new TokenizeResponse(token, lastFour, cardBrand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("Card token not found or revoked", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;

        try {
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());
            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest request = PaymentProcessorRequest
                    .card(paymentId, pan, expiry, amount, methodDetails);

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(request);
            log.info("Vault charge registered, token: {}*****", token.substring(0, 4));
            return paymentProcessorResponse;

        } catch (Exception e) {
            log.error("Vault Charge Failed for token: {}", token);
            return new PaymentProcessorResponse.Failure("Vault Charge Failed", e.getMessage());
        } finally {
            if(panBytes != null) Arrays.fill(panBytes, (byte) 0); // Clear the PAN bytes from memory
        }
    }

    private CardBrand detectBrand(String pan) {
        if (pan.startsWith("4")) {
            return CardBrand.VISA;
        } else if (pan.startsWith("5") || pan.startsWith("2")) {
            return CardBrand.MASTERCARD;
        } else if (pan.startsWith("37") || pan.startsWith("34")) {
            return CardBrand.AMEX;
        }
        return CardBrand.RUPAY;
    }
}
