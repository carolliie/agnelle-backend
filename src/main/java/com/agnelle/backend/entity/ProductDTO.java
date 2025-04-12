package com.agnelle.backend.entity;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(String name, String size, BigDecimal price, List<String> images, List<Category> categories, String slug) {
}
