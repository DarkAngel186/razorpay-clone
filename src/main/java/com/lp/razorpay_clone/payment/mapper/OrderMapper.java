package com.lp.razorpay_clone.payment.mapper;

import com.lp.razorpay_clone.payment.dto.response.OrderResponse;
import com.lp.razorpay_clone.payment.entity.OrderRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(OrderRecord orderRecord);
}
