package com.agnelle.backend.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record ProductDTO(Long id, String name, String size, BigDecimal price, Date date, List<String> images, List<String> categories, String slug) {
}
