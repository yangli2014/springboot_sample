package com.yang.product.product.repo;

import com.yang.product.product.entity.Approval;
import com.yang.product.product.entity.ApprovalStatus;
import com.yang.product.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepo extends JpaRepository<Approval, Long> {
    List<Approval> findByStatusOrderByRequestTimeAsc(ApprovalStatus status);
    List<Approval> findByProduct(Product product);
}
