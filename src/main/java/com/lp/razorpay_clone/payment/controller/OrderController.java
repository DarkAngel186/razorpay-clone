package com.lp.razorpay_clone.payment.controller;

import com.lp.razorpay_clone.payment.dto.request.CreateOrderRequest;
import com.lp.razorpay_clone.payment.dto.response.OrderResponse;
import com.lp.razorpay_clone.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest order) {
        OrderResponse createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }
}
