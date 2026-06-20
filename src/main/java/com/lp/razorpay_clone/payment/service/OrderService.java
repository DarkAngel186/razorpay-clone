package com.lp.razorpay_clone.payment.service;

import com.lp.razorpay_clone.payment.dto.request.CreateOrderRequest;
import com.lp.razorpay_clone.payment.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest order);
}
