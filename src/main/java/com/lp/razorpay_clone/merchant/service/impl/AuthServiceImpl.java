package com.lp.razorpay_clone.merchant.service.impl;

import com.lp.razorpay_clone.common.enums.MerchantStatus;
import com.lp.razorpay_clone.common.enums.UserRole;
import com.lp.razorpay_clone.common.exception.DuplicateResourceException;
import com.lp.razorpay_clone.merchant.dto.response.MerchantResponse;
import com.lp.razorpay_clone.merchant.dto.request.MerchantSignupRequest;
import com.lp.razorpay_clone.merchant.entity.AppUser;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import com.lp.razorpay_clone.merchant.repository.AppUserRepository;
import com.lp.razorpay_clone.merchant.repository.MerchantRepository;
import com.lp.razorpay_clone.merchant.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    MerchantRepository merchantRepository;
    AppUserRepository appUserRepository;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {

        if(merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL", "Email : "+ request.email() + " already exists!!");
        }

        Merchant merchant = Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessType(request.businessType())
                .businessName(request.businessName())
                .status(MerchantStatus.PENDING_KYC)
                .build();

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .password(request.password())  //TODO: encrypt password
                .merchant(merchant)
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .status(merchant.getStatus())
                .businessName(merchant.getBusinessName())
                .businessType(merchant.getBusinessType())
                .build();
    }
}
