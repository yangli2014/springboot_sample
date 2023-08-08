package com.yang.product.product.repo;

import com.yang.product.product.entity.Product;
import com.yang.product.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus productStatus);
}
