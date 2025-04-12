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
    @Autowired
    private StorageService storageService;

    public Product saveProduct(Product product, MultipartFile[] images) throws IOException {
        String result = slg.slugify(product.getName());
        product.setDate(new Date());
        product.setSlug(result);

        List<Category> categories = product.getCategories().stream()
                .map(category -> categoryRepository.findByCategorySlug(category.getCategorySlug())
                        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + category.getCategorySlug())))
                .collect(Collectors.toList());
        product.setCategories(categories);

        if (images != null && images.length > 0) {
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile image : images) {
                String imageUrl = storageService.storeImage(image);
                imageUrls.add(imageUrl);
            }

            product.setImages(imageUrls);
        }

        return productRepository.save(product);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
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

    public Product editProductBySlug(String productSlug, ProductDTO productDto, MultipartFile[] images) throws IOException {
        Optional<Product> optionalProduct = productRepository.findBySlug(productSlug);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            if (productDto.name() != null) {
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
                        .map(category -> categoryRepository.findByCategorySlug(category.getCategorySlug())
                                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + category)))
                        .collect(Collectors.toList());
                product.setCategories(categories);
            }
            if (productDto.images() != null) {
                if (images != null && images.length > 0) {
                    List<String> imageUrls = new ArrayList<>();

                    for (MultipartFile image : images) {
                        String imageUrl = storageService.storeImage(image);
                        imageUrls.add(imageUrl);
                    }

                    product.setImages(imageUrls);
                }
                product.setImages(productDto.images());
            }

            productRepository.save(product);
            return product;
        } else {
            throw new EntityNotFoundException("product not found or deleted.");
        }
    }
}
