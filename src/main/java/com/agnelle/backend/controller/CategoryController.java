package com.agnelle.backend.controller;

import com.agnelle.backend.entity.Category;
import com.agnelle.backend.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            Category createdCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{categorySlug}")
    public ResponseEntity<?> getCategory(@PathVariable String categorySlug) {
        try {
            Category category = categoryService.getCategoryBySlug(categorySlug);

            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{categorySlug}")
    public ResponseEntity<?> deleteCategory(@PathVariable String categorySlug) {
        try {
            Category category = categoryService.deleteCategoryBySlug(categorySlug);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/{categorySlug}")
    public ResponseEntity<?> editCategoryBySlug(@PathVariable String categorySlug, @RequestBody Category category) {
        try {
            Category categoryToEdit = categoryService.editCategoryBySlug(categorySlug, category);
            return ResponseEntity.ok(categoryToEdit);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
