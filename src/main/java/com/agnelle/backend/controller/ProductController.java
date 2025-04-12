package com.agnelle.backend.controller;

import com.agnelle.backend.entity.Product;
import com.agnelle.backend.entity.ProductDTO;
import com.agnelle.backend.service.ProductService;
import com.agnelle.backend.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product, @RequestParam("images") MultipartFile[] images) {
        try {
            Product createdProduct = productService.saveProduct(product, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getProduct(@PathVariable String slug) {
        try {
            Product product = productService.getProductBySlug(slug);

            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        try {
            Product product = productService.deleteProductById(productId);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/{productSlug}")
    public ResponseEntity<?> editProductBySlug(@PathVariable String productSlug, @RequestBody ProductDTO productDTO, MultipartFile[] images) {
        try {
            Product product = productService.editProductBySlug(productSlug, productDTO, images);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
