package com.lp.razorpay_clone.merchant.service.impl;

import com.lp.razorpay_clone.common.enums.MerchantStatus;
import com.lp.razorpay_clone.common.enums.UserRole;
import com.lp.razorpay_clone.common.exception.DuplicateResourceException;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.merchant.dto.request.LoginRequest;
import com.lp.razorpay_clone.merchant.dto.response.LoginResponse;
import com.lp.razorpay_clone.merchant.dto.response.MerchantResponse;
import com.lp.razorpay_clone.merchant.dto.request.MerchantSignupRequest;
import com.lp.razorpay_clone.merchant.entity.AppUser;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import com.lp.razorpay_clone.merchant.mapper.MerchantMapper;
import com.lp.razorpay_clone.merchant.repository.AppUserRepository;
import com.lp.razorpay_clone.merchant.repository.MerchantRepository;
import com.lp.razorpay_clone.merchant.security.JwtUtil;
import com.lp.razorpay_clone.merchant.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    MerchantRepository merchantRepository;
    AppUserRepository appUserRepository;
    MerchantMapper merchantMapper;

    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {

        if(merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL", "Email : "+ request.email() + " already exists!!");
        }

        Merchant merchant = merchantMapper.toMerchantEntity(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return merchantMapper.toMerchantResponse(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("AppUser", request.email()));

        String accessToken = jwtUtil.generateAcsessToken(appUser.getEmail(), appUser.getMerchant().getId(), appUser.getRole().toString());
        return new LoginResponse(accessToken);
    }
}
