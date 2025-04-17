package com.agnelle.backend.entity;

import java.util.Date;

public record CategoryDTO(Long id, String name, String categorySlug, Date date, String image) {
}
