package com.demo.order.dto;

public record PaymentRequest(Long orderId, String username, Long productId, Integer quantity) {}
