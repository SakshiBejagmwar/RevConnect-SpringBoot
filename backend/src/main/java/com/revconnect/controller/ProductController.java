package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.entity.Product;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private UserRepository userRepository;

    // Add product (authenticated business user)
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            product.setBusinessUserId(userId);
            Product saved = service.addProduct(product);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get my products
    @GetMapping
    public ResponseEntity<List<Product>> getMyProducts(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Product> products = service.getProductsByBusiness(userId);
        return ResponseEntity.ok(products);
    }

    // Get products by business user ID (public)
    @GetMapping("/business/{businessUserId}")
    public ResponseEntity<List<Product>> getProductsByBusiness(@PathVariable Long businessUserId) {
        List<Product> products = service.getProductsByBusiness(businessUserId);
        return ResponseEntity.ok(products);
    }

    // Delete product (with authorization)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            service.deleteProduct(id, userId);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOpt.get().getId();
    }
}
