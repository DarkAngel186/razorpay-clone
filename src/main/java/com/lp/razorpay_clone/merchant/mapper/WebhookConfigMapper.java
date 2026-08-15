package com.lp.razorpay_clone.merchant.mapper;

import com.lp.razorpay_clone.merchant.dto.response.WebhookConfigResponse;
import com.lp.razorpay_clone.merchant.entity.MerchantWebhookConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WebhookConfigMapper {

    @Mapping(target = "webhookSecret", source = "rawSecret")
    WebhookConfigResponse toResponse(MerchantWebhookConfig config, String rawSecret);
}
