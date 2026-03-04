package com.demo.user.dto;

public record OrderResponse(Long orderId, String status, String paymentStatus) {}
