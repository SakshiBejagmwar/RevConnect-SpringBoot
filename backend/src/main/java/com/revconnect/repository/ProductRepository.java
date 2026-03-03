package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByBusinessUserId(Long businessUserId);
}
