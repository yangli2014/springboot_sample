package com.yang.product.product.controler;

import com.yang.product.product.dto.ApprovalDTO;
import com.yang.product.product.dto.ProductDTO;
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
    public ResponseEntity<Object> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            return ResponseEntity.ok(service.createNewProduct(productDTO));
        } catch (ProductException e) {
            LOG.error("Product Error while creating new product {}", productDTO, e);
            return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while creating new product {}", productDTO, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Object> updateProduct(@PathVariable long productId, @RequestBody ProductDTO dto) {
        try {
            return ResponseEntity.ok(service.updateProduct(productId, dto));
        } catch (ProductException e) {
            LOG.error("Error while updating product with ID {}", productId, e);
            return new ResponseEntity<>(e.getMessage(),e.getHttpStatus());
        } catch (Exception e) {
            LOG.error("Error while updating product with ID {} and data {}", productId, dto, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/approval-queue")
    public List<ApprovalDTO> listApproval() {
        return service.listAllNewApproval();
    }
}
