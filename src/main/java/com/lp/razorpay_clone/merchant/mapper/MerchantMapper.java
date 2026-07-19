package com.lp.razorpay_clone.merchant.mapper;

import com.lp.razorpay_clone.merchant.dto.request.MerchantSignupRequest;
import com.lp.razorpay_clone.merchant.dto.response.MerchantResponse;
import com.lp.razorpay_clone.merchant.entity.Merchant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    Merchant toMerchantEntity(MerchantSignupRequest request);

    MerchantResponse toMerchantResponse(Merchant entity);
}
