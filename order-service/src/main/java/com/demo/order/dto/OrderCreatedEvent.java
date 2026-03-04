package com.demo.order.dto;

import java.time.Instant;

public record OrderCreatedEvent(Long orderId, String username, Long productId, Integer quantity, Instant createdAt) {}
