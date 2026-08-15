package com.lp.razorpay_clone.vault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class VaultEncryptionConfig {

    public static BytesEncryptor panEncryptor(byte[] dek) {
        SecretKeySpec secretKey = new SecretKeySpec(dek, "AES");
        return new AesBytesEncryptor(secretKey,
                KeyGenerators.secureRandom(12),
                AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
