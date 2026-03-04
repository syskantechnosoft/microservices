package com.demo.payment.dto;

import java.time.Instant;

public record PaymentCompletedEvent(Long orderId, String username, String status, Instant processedAt) {}
