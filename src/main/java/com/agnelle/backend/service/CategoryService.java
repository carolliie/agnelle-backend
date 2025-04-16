package com.agnelle.backend.service;

import com.agnelle.backend.entity.Category;
import com.agnelle.backend.entity.CategoryDTO;
import com.agnelle.backend.entity.Product;
import com.agnelle.backend.entity.ProductDTO;
import com.agnelle.backend.repository.CategoryRepository;
import com.github.slugify.Slugify;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    final Slugify slg = Slugify.builder().build();

    @Autowired
    CategoryRepository categoryRepository;

    public Category saveCategory(Category category) throws IOException {
        String result = slg.slugify(category.getName());
        category.setCategorySlug(result);

        return categoryRepository.save(category);
    }

    public List<CategoryDTO> getCategories() {

        List<Category> categories = categoryRepository.findAll();

        List<CategoryDTO> categoryDTOs = categories.stream().map(category -> {
            CategoryDTO categoryDTO = new CategoryDTO(
                    category.getId(),
                    category.getName(),
                    category.getCategorySlug()
            );

            return categoryDTO;
        }).collect(Collectors.toList());

        return categoryDTOs;
    }

    public Category getCategoryBySlug(String slug) {
        Optional<Category> category = categoryRepository.findByCategorySlug(slug);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new EntityNotFoundException("category not found");
        }
    }

    public Category deleteCategoryBySlug(String slug) {
        Optional<Category> category = categoryRepository.findByCategorySlug(slug);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return category.get();
        } else {
            throw new EntityNotFoundException("category not found or deleted.");
        }
    }

    public Category editCategoryBySlug(String slug, Category category) {
        Optional<Category> optionalCategory = categoryRepository.findByCategorySlug(slug);

        if (optionalCategory.isPresent()) {
            Category newCategory = optionalCategory.get();

            if (category.getName() != null) {
                newCategory.setName(category.getName());
                String newSlug = slg.slugify(category.getName());
                category.setCategorySlug(newSlug);
            }

            categoryRepository.save(category);
            return category;
        } else {
            throw new EntityNotFoundException("category not found or deleted.");
        }
    }
}
