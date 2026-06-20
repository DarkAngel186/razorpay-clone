package com.lp.razorpay_clone.payment.service.impl;

import com.lp.razorpay_clone.common.exception.DuplicateResourceException;
import com.lp.razorpay_clone.payment.dto.request.CreateOrderRequest;
import com.lp.razorpay_clone.payment.dto.response.OrderResponse;
import com.lp.razorpay_clone.payment.entity.OrderRecord;
import com.lp.razorpay_clone.payment.repository.OrderRecordRepository;
import com.lp.razorpay_clone.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRecordRepository orderRecordRepository;

    UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");   //TODO: Replace with Merchant Context

    @Value("${order.default.expire.time:15}")
    private int defaultOrderExpireTime;

    @Override
    public OrderResponse createOrder(CreateOrderRequest order) {

        if(order.receipt() != null && orderRecordRepository.existsByMerchantIdAndReceipt(merchantId, order.receipt())) {
            throw new DuplicateResourceException("DUPLICATE_RECEIPT", "Order with receipt: " + order.receipt() + " already exists!!");
        }

        OrderRecord orderRecord = OrderRecord.builder()
                        .merchantId(merchantId)
                        .amount(order.amount())
                        .receipt(order.receipt())
                        .notes(order.notes())
                        .expiresAt(order.expiresAt() != null ? order.expiresAt() : LocalDateTime.now().plusMinutes(defaultOrderExpireTime))  //TODO: Replace with Configurable Default Expiry
                        .build();

        orderRecord =  orderRecordRepository.save(orderRecord);

        return new OrderResponse(
                orderRecord.getId(),
                orderRecord.getMerchantId(),
                orderRecord.getReceipt(),
                orderRecord.getAmount(),
                orderRecord.getStatus(),
                orderRecord.getNotes(),
                orderRecord.getExpiresAt(),
                null
        );
    }
}
