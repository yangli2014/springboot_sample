package com.yang.product.product.service;

import com.yang.product.product.dto.ProductDTO;
import com.yang.product.product.entity.Approval;
import com.yang.product.product.entity.Product;
import com.yang.product.product.exception.ProductException;
import com.yang.product.product.repo.ApprovalRepo;
import com.yang.product.product.repo.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepo productRepo;
    @Mock
    private ApprovalRepo approvalRepo;
    @InjectMocks
    private ProductService service;

    float priceLimit = 10000f;
    float princePending = 5000f;
    float updatePending = 50; //percentage

    @BeforeEach
    void setUp (){
        service.setPriceLimitExceed(priceLimit);
        service.setPriceLimitPending(princePending);
        service.setPriceLimitUpdate(updatePending);
    }

    @Test
    void testNewProductExceedLimit() {
        ProductDTO product = new ProductDTO();
        product.setName("test");
        product.setPrice(10001F);
        product.setStatus("ACTIVE");
        assertThatThrownBy(() -> service.createNewProduct(product))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("The product price exceed the")
                .extracting("httpStatus").isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(productRepo);
        verifyNoInteractions(approvalRepo);
    }

    @Test
    void testNewProductNeedToApprove() throws ProductException {
        ProductDTO product = new ProductDTO();
        product.setName("test");
        product.setPrice(5001F);
        product.setStatus("ACTIVE");
        ProductDTO newProduct = service.createNewProduct(product);
        assertThat(newProduct).isNotNull()
                .extracting(ProductDTO::getStatus).isEqualTo("PENDING");
        verify(productRepo).save(any(Product.class));
        verify(approvalRepo).save(any(Approval.class));
    }

    @Test
    void testNewProductSuccess() throws ProductException {
        ProductDTO product = new ProductDTO();
        product.setName("test");
        product.setPrice(500F);
        product.setStatus("ACTIVE");
        ProductDTO newProduct = service.createNewProduct(product);
        assertThat(newProduct).isNotNull()
                .extracting(ProductDTO::getStatus).isEqualTo("ACTIVE");
        verify(productRepo).save(any(Product.class));
        verifyNoInteractions(approvalRepo);
    }
}
