package com.lp.razorpay_clone.payment.service.impl;

import com.lp.razorpay_clone.common.enums.OrderStatus;
import com.lp.razorpay_clone.common.exception.BusinessRuleViolationException;
import com.lp.razorpay_clone.common.exception.DuplicateResourceException;
import com.lp.razorpay_clone.common.exception.ResourceNotFoundException;
import com.lp.razorpay_clone.payment.dto.request.CreateOrderRequest;
import com.lp.razorpay_clone.payment.dto.response.OrderResponse;
import com.lp.razorpay_clone.payment.dto.response.PaymentResponse;
import com.lp.razorpay_clone.payment.entity.OrderRecord;
import com.lp.razorpay_clone.payment.mapper.OrderMapper;
import com.lp.razorpay_clone.payment.mapper.PaymentMapper;
import com.lp.razorpay_clone.payment.repository.OrderRecordRepository;
import com.lp.razorpay_clone.payment.repository.PaymentRepository;
import com.lp.razorpay_clone.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRecordRepository orderRecordRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${order.default.expire.time:15}")
    private int defaultOrderExpireTime;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID merchantId, CreateOrderRequest order) {

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

        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public OrderResponse getOrderById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order with id: " + orderId + " not found!!"));

        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order with id: " + orderId + " not found!!"));

        if(orderRecord.getStatus() == OrderStatus.CANCELLED ||  orderRecord.getStatus() == OrderStatus.PAID) {
            throw  new BusinessRuleViolationException("ORDER_CANNOT_BE_CANCELLED", "Order with id: " + orderId + " cannot be cancelled!!");
        }

        orderRecord.setStatus(OrderStatus.CANCELLED);
        orderRecord = orderRecordRepository.save(orderRecord);
        return orderMapper.toOrderResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> getPayments(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order with id: " + orderId + " not found!!"));

        return paymentMapper.toPaymentResponseList(
                paymentRepository.findByOrder_Id(orderRecord)
        );
    }
}
