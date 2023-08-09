package com.yang.product.product.service;

import com.yang.product.product.dto.ApprovalDTO;
import com.yang.product.product.dto.ProductDTO;
import com.yang.product.product.dto.SearchCriteria;
import com.yang.product.product.entity.*;
import com.yang.product.product.exception.ApprovalException;
import com.yang.product.product.exception.ProductException;
import com.yang.product.product.repo.ApprovalRepo;
import com.yang.product.product.repo.ProductRepo;
import com.yang.product.product.repo.ProductSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    Logger LOG = LoggerFactory.getLogger(ProductService.class);
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

    public List<ProductDTO> searchProducts(SearchCriteria criteria) {
        ProductSpecification spec = new ProductSpecification(criteria);
        return productRepo.findAll(spec)
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
            productRepo.save(product);
            createNewApproval(Operation.ADD, "Created a new product with price exceed $" + priceLimitPending +
                    "the request data", product, product.getPrice());
        } else {
            productRepo.save(product);
        }
        return entityToDTO(product);
    }

    public ProductDTO updateProduct(long productId, ProductDTO dto) throws ProductException {
        Product product = findProductById(productId);
        if(dto.getPrice() <=0) {
            throw new ProductException("The product price must be greater than zero.", HttpStatus.BAD_REQUEST);
        }
        float oldPrice = product.getPrice();
        product.setName(dto.getName());
        product.setStatus(ProductStatus.valueOf(dto.getStatus()));
        if((dto.getPrice() - oldPrice) / oldPrice > priceLimitUpdate / 100) {
          createNewApproval(Operation.UPDATE, "The product price will be increased more than " + priceLimitUpdate
                  +"%.", product, dto.getPrice());
        } else {
            product.setPrice(dto.getPrice());
            productRepo.save(product);
        }
        return entityToDTO(product);
    }

    public void deleteProduct(long productId) throws ProductException {
        Product product = findProductById(productId);
        createNewApproval(Operation.DELETE,"Delete Product with ID: " + productId, product, product.getPrice());
    }

    private Product findProductById(long productId) throws ProductException {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductException("Product with specified id: "
                + productId + " doesn't exist", HttpStatus.NOT_FOUND));
        return product;
    }

    public List<ApprovalDTO> listAllNewApproval() {
        return approvalRepo.findByStatusOrderByRequestTimeAsc(ApprovalStatus.NEW)
                .stream().map(a -> new ApprovalDTO(a)).toList();
    }

    public void approveProduct(long approvalId) throws ApprovalException {
        Approval approval = findApproval(approvalId);
        Product product = approval.getProduct();
        switch (approval.getOperation()) {
            case ADD -> product.setStatus(ProductStatus.ACTIVE);
            case DELETE -> {
                product.setStatus(ProductStatus.DELETED);
                approvalRepo.findByProduct(product)
                        .stream().filter(a -> a.getId() != approvalId)
                        .forEach(a -> {
                            try {
                                rejectApproval(a.getId());
                            } catch (ApprovalException e) {
                                LOG.error("error while reject approval for deleted product", e);
                            }
                        });
            }
            case UPDATE -> product.setPrice(approval.getNewPrice());
        }
        productRepo.save(product);
        approval.setStatus(ApprovalStatus.APPROVED);
        approvalRepo.save(approval);
    }

    public void rejectApproval(long approvalId) throws ApprovalException {
        Approval approval = findApproval(approvalId);
        approval.setStatus(ApprovalStatus.REJECTED);
        Product product = approval.getProduct();
        if(approval.getOperation().equals(Operation.ADD) && product.getStatus().equals(ProductStatus.PENDING)) {
            product.setStatus(ProductStatus.DELETED);
            productRepo.save(product);
        }
        approvalRepo.save(approval);
    }

    private Approval findApproval(long approvalId) throws ApprovalException {
        return approvalRepo.findById(approvalId)
                .orElseThrow(() -> new ApprovalException("Approval with id " + approvalId + " doesn't exist",
                        HttpStatus.NOT_FOUND));
    }

    private void createNewApproval(Operation operation, String reason, Product product, float newPrice) {
        Approval approval = new Approval();
        approval.setStatus(ApprovalStatus.NEW);
        approval.setReason(reason);
        approval.setProduct(product);
        approval.setOperation(operation);
        approval.setNewPrice(newPrice);
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
