package com.agnelle.backend.service;

import com.agnelle.backend.entity.Category;
import com.agnelle.backend.entity.Product;
import com.agnelle.backend.entity.ProductDTO;
import com.agnelle.backend.repository.CategoryRepository;
import com.agnelle.backend.repository.ProductRepository;
import com.github.slugify.Slugify;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService {

    final Slugify slg = Slugify.builder().build();

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    public Product saveProduct(Product product) throws IOException {
        String result = slg.slugify(product.getName());
        product.setDate(new Date());
        product.setSlug(result);

        return productRepository.save(product);
    }

    public List<ProductDTO> getProducts() {

        List<Product> products = productRepository.findAll();

        List<ProductDTO> productDTOs = products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO(
                    product.getName(),
                    product.getSize(),
                    product.getPrice(),
                    product.getDate(),
                    product.getImages(),
                    product.getCategories().stream()
                            .map(category -> category.getName())
                            .collect(Collectors.toList()),
                    product.getSlug()
            );

            return productDTO;
        }).collect(Collectors.toList());

        return productDTOs;
    }

    public Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new EntityNotFoundException("product not found");
        }
    }

    public Product getProductBySlug(String slug) {
        Optional<Product> product = productRepository.findBySlug(slug);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new EntityNotFoundException("product not found");
        }
    }

    public Product deleteProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return product.get();
        } else {
            throw new EntityNotFoundException("product not found or deleted.");
        }
    }

    public Product editProductBySlug(String productSlug, ProductDTO productDto) throws IOException {
        Optional<Product> optionalProduct = productRepository.findBySlug(productSlug);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            if (productDto.name() != null && !productDto.name().equals(product.getName())) {
                product.setName(productDto.name());
                String newSlug = slg.slugify(productDto.name());
                product.setSlug(newSlug);
            }

            if (productDto.size() != null) {
                product.setSize(productDto.size());
            }
            if (productDto.price() != null) {
                product.setPrice(productDto.price());
            }

            if (productDto.categories() != null) {
                List<Category> categories = productDto.categories().stream()
                        .map(category -> categoryRepository.findByCategorySlug(category)
                                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + category)))
                        .collect(Collectors.toList());
                product.setCategories(categories);
            }

            if (productDto.images() != null) {
                product.setImages(productDto.images());
            }

            productRepository.save(product);
            return product;
        } else {
            throw new EntityNotFoundException("product not found or deleted.");
        }
    }
}
