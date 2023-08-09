package com.yang.product.product.controller;

import com.yang.product.product.dto.ProductDTO;
import com.yang.product.product.entity.ProductStatus;
import com.yang.product.product.exception.ProductException;
import com.yang.product.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {
    @Autowired
    private ProductController controller;
    @MockBean
    private ProductService service;
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int serverPort;

    private ProductDTO product;
    private String url;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        product = new ProductDTO();
        product.setName("test");
        product.setStatus("ACTIVE");
        product.setPrice(100F);

        url = "http://localhost:" + serverPort + "/api/products";
        headers = new HttpHeaders();
        headers.add("Content-type", "application/json");
        headers.add("Accept", "application/json");
    }
    @Test
    void testCreateProduct() throws ProductException, URISyntaxException {
        ProductDTO responseDto = new ProductDTO();
        responseDto.setStatus(product.getStatus());
        doReturn(responseDto).when(service).createNewProduct(any(ProductDTO.class));
        HttpEntity<ProductDTO> request = new HttpEntity<>(product, headers);
        ResponseEntity<ProductDTO> result = restTemplate.postForEntity(url, request, ProductDTO.class);
        assertThat(result.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        verify(service).createNewProduct(any(ProductDTO.class));
    }

    @Test
    void testCreateProductPending() throws ProductException, URISyntaxException {
        ProductDTO responseDto = new ProductDTO();
        responseDto.setStatus(ProductStatus.PENDING.name());
        doReturn(responseDto).when(service).createNewProduct(any(ProductDTO.class));
        //URI uri = new URI(url);
        HttpEntity<ProductDTO> request = new HttpEntity<>(product, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
        assertThat(result.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(result.getBody()).contains("request was sent and waiting for approval");
        verify(service).createNewProduct(any(ProductDTO.class));
    }

    @Test
    void testCreateProductException() throws ProductException, URISyntaxException {
        doThrow(new ProductException("The product price is invalid", HttpStatus.BAD_REQUEST)).when(service).createNewProduct(any(ProductDTO.class));
        HttpEntity<ProductDTO> request = new HttpEntity<>(product, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
        assertThat(result.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getBody()).contains("The product price is invalid");
        verify(service).createNewProduct(any(ProductDTO.class));
    }
}
