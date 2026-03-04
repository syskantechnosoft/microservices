package com.demo.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotBlank String username, @NotNull Long productId, @Min(1) Integer quantity) {}
