package com.yang.product.product.controler;

import com.yang.product.product.dto.ApprovalDTO;
import com.yang.product.product.dto.ProductDTO;
import com.yang.product.product.entity.ProductStatus;
import com.yang.product.product.exception.ApprovalException;
import com.yang.product.product.exception.ProductException;
import com.yang.product.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private static Logger LOG = LoggerFactory.getLogger(ProductController.class);
    private final ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDTO> listProducts() {
        return service.listActiveProducts();
    }
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO newProduct = service.createNewProduct(productDTO);
            if(ProductStatus.ACTIVE.name().equals(newProduct.getStatus())) {
                return ResponseEntity.ok(newProduct);
            } else {
                return ResponseEntity.accepted().body("Adding new product request was sent and waiting for approval");
            }
        } catch (ProductException e) {
            LOG.error("Product Error while creating new product {}", productDTO, e);
            return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while creating new product {}", productDTO, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable long productId, @RequestBody ProductDTO dto) {
        try {
            ProductDTO updated = service.updateProduct(productId, dto);
            if(updated.getPrice() == dto.getPrice()) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.accepted().body("Price updating request was sent and waiting for approval");
            }
        } catch (ProductException e) {
            LOG.error("Error while updating product with ID {}", productId, e);
            return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while updating product with ID {} and data {}", productId, dto, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable long productId) {
        try {
            service.deleteProduct(productId);
            return ResponseEntity.accepted().body("Deleting product request was sent and waiting for approval.");
        } catch (ProductException e) {
            LOG.error("Error while deleting product with ID {}", productId, e);
            return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while deleting product with ID {}", productId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/approval-queue")
    public List<ApprovalDTO> listApproval() {
        return service.listAllNewApproval();
    }

    @PutMapping("/approval-queue/{approvalId}/approve")
    public ResponseEntity<?> approveProduct(@PathVariable long approvalId) {
        try {
            service.approveProduct(approvalId);
            return ResponseEntity.noContent().build();
        } catch (ApprovalException e) {
            LOG.error("Error while approving product change with approval ID {}", approvalId, e);
            return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while approving with approval id {}", approvalId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/approval-queue/{approvalId}/reject")
    public ResponseEntity<?> rejectProduct(@PathVariable long approvalId) {
        try {
            service.rejectApproval(approvalId);
            return ResponseEntity.noContent().build();
        } catch (ApprovalException e) {
            LOG.error("Error while rejecting product change with approval ID {}", approvalId, e);
            return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
        }
    }
}
