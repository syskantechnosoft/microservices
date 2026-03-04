package com.demo.payment.dto;

public record PaymentRequest(Long orderId, String username, Long productId, Integer quantity) {}
