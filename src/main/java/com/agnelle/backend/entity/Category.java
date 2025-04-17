package com.agnelle.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String categorySlug;

    @ManyToMany(mappedBy = "categories")
    private List<Product> products;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String image;
}
