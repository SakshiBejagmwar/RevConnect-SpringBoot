package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revconnect.entity.Product;
import com.revconnect.repository.ProductRepository;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product addProduct(Product product) {
        return repository.save(product);
    }

    public List<Product> getProductsByBusiness(Long businessUserId) {
        return repository.findByBusinessUserId(businessUserId);
    }

    public void deleteProduct(Long id, Long authenticatedUserId) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Authorization: Only the business owner can delete their product
        if (!product.getBusinessUserId().equals(authenticatedUserId)) {
            throw new RuntimeException("You are not authorized to delete this product");
        }
        
        repository.deleteById(id);
    }
}
