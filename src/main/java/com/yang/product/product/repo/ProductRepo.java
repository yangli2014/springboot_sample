package com.yang.product.product.repo;

import com.yang.product.product.entity.Product;
import com.yang.product.product.entity.ProductStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByStatus(ProductStatus productStatus);
}
