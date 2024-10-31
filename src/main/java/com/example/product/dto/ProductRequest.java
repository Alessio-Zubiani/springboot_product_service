package com.example.product.dto;

import java.math.BigDecimal;

public record ProductRequest(long id, String name, String description, BigDecimal price) {}