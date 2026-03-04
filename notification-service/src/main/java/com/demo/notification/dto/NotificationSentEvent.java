package com.demo.notification.dto;

import java.time.Instant;

public record NotificationSentEvent(Long orderId, String username, String status, Instant sentAt) {}
