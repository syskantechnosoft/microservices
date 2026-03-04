package com.demo.user.dto;

import java.math.BigDecimal;

public record ProductResponseV2(Long id, String name, BigDecimal price, Integer stock, String currency) {}
