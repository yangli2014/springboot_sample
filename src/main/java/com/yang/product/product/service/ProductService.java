package com.yang.product.product.service;

import com.yang.product.product.dto.ApprovalDTO;
import com.yang.product.product.dto.ProductDTO;
import com.yang.product.product.entity.Approval;
import com.yang.product.product.entity.ApprovalStatus;
import com.yang.product.product.entity.Product;
import com.yang.product.product.entity.ProductStatus;
import com.yang.product.product.exception.ProductException;
import com.yang.product.product.repo.ApprovalRepo;
import com.yang.product.product.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final ApprovalRepo approvalRepo;
    @Value("${product.price.limit.exceed}")
    private float priceLimitExceed;
    @Value("${product.price.limit.pending}")
    private float priceLimitPending;

    @Value("${product.price.limit.update}")
    private float priceLimitUpdate;
    @Autowired
    public ProductService(ProductRepo productRepo, ApprovalRepo approvalRepo) {
        this.productRepo = productRepo;
        this.approvalRepo = approvalRepo;
    }

    public List<ProductDTO> listActiveProducts() {
        return productRepo.findByStatus(ProductStatus.ACTIVE)
                .stream().map(p -> entityToDTO(p)).toList();
    }

    public ProductDTO createNewProduct(ProductDTO newProduct) throws ProductException {
        if(newProduct.getPrice()> priceLimitExceed) {
            throw new ProductException("Error: The product price exceed the $" + priceLimitExceed, HttpStatus.BAD_REQUEST);
        }
        if(newProduct.getPrice() <=0) {
            throw new ProductException("The product price must be greater than zero.", HttpStatus.BAD_REQUEST);
        }
        Product product = dtoToEntity(newProduct);
        if(product.getPrice() > priceLimitPending) {
            product.setStatus(ProductStatus.PENDING);
            createNewApproval("Created a new product with price exceed $" + priceLimitPending + ", please verify " +
                    "the request data", product);
        }
        productRepo.save(product);
        return entityToDTO(product);
    }

    public ProductDTO updateProduct(long productId, ProductDTO dto) throws ProductException {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductException("Product with specified id: "
                + productId + " doesn't exist", HttpStatus.NOT_FOUND));
        if(dto.getPrice() <=0) {
            throw new ProductException("The product price must be greater than zero.", HttpStatus.BAD_REQUEST);
        }
        float oldPrice = product.getPrice();
        product.setPrice(dto.getPrice());
        product.setName(dto.getName());
        product.setStatus(ProductStatus.valueOf(dto.getStatus()));
        if((dto.getPrice() - oldPrice) / oldPrice > priceLimitUpdate/ 100) {
          product.setStatus(ProductStatus.UPDATED);
          createNewApproval("The product price will be increased more than " + priceLimitUpdate +"%.", product);
        }
        productRepo.save(product);
        return entityToDTO(product);
    }

    public List<ApprovalDTO> listAllNewApproval() {
        return approvalRepo.findByStatusOrderByRequestTimeAsc(ApprovalStatus.NEW)
                .stream().map(a -> new ApprovalDTO(a)).toList();
    }

    private void createNewApproval(String reason, Product product) {
        Approval approval = new Approval();
        approval.setStatus(ApprovalStatus.NEW);
        approval.setReason(reason);
        approval.setProduct(product);
        approvalRepo.save(approval);
    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setStatus(product.getStatus().name());
        dto.setPrice(product.getPrice());
        dto.setPostDate(product.getPostDate());
        return dto;
    }

    private Product dtoToEntity(ProductDTO dto) {
        Product product = new Product(dto.getName(), dto.getPrice(), ProductStatus.valueOf(dto.getStatus().toUpperCase()));
        return product;
    }
}
