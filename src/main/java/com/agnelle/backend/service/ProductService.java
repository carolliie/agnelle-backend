package com.agnelle.backend.service;

import com.agnelle.backend.entity.Product;
import com.agnelle.backend.entity.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product saveProduct(Product product) throws IOException;
    List<ProductDTO> getProducts();
    Product getProductBySlug(String slug);
    Product getProductById(Long productId);
    Product editProductBySlug(String slug, ProductDTO productDTO) throws IOException;
    Product deleteProductById(Long id);

}
