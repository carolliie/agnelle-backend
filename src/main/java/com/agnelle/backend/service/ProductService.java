package com.agnelle.backend.service;

import com.agnelle.backend.entity.Product;
import com.agnelle.backend.entity.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product saveProduct(Product product, MultipartFile[] images) throws IOException;
    List<Product> getProducts();
    Product getProductBySlug(String slug);
    Product getProductById(Long productId);
    Product editProductBySlug(String slug, ProductDTO productDTO, MultipartFile[] images) throws IOException;
    Product deleteProductById(Long id);

}
