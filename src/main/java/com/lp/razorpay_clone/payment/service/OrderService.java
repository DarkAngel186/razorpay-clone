package com.lp.razorpay_clone.payment.service;

import com.lp.razorpay_clone.payment.dto.request.CreateOrderRequest;
import com.lp.razorpay_clone.payment.dto.response.OrderResponse;
import com.lp.razorpay_clone.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID merchantId, CreateOrderRequest order);

    OrderResponse getOrderById(UUID merchantId, UUID orderId);

    OrderResponse cancelOrder(UUID merchantId, UUID orderId);

    List<PaymentResponse> getPayments(UUID merchantId, UUID orderId);
}
