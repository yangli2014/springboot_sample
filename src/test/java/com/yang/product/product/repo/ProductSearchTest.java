package com.yang.product.product.repo;

import com.yang.product.product.dto.SearchCriteria;
import com.yang.product.product.entity.Product;
import com.yang.product.product.entity.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ProductSearchTest {
    @Autowired
    private ProductRepo repo;

    Product book1, book2, book3, tv;

    @BeforeEach
    void setUp() {
        book1 = new Product("Math text book", 10.25f, ProductStatus.ACTIVE);
        book1.setPostDate(LocalDate.of(2023,8, 1));

        book2 = new Product("Book with pictures", 30.56f, ProductStatus.ACTIVE);
        book2.setPostDate(LocalDate.of(2023,8, 3));

        book3 = new Product("Book about history", 50.0f, ProductStatus.ACTIVE);
        book3.setPostDate(LocalDate.of(2023,8, 7));

        tv = new Product("LCD TV", 500.00f, ProductStatus.PENDING);
        tv.setPostDate(LocalDate.of(2023,8, 8));

        repo.saveAll(Arrays.asList(book1, book2, book3, tv));
    }

    @Test
    void testSearchWithoutCriteria() {
        List<Product> result = repo.findAll();
        assertThat(result)
                .extracting("name")
                .contains(book1.getName(), book2.getName(), book3.getName(), tv.getName()).size().isEqualTo(4);
    }

    @Test
    void testListByStatus() {
        List<Product> result = repo.findByStatus(ProductStatus.ACTIVE);
        assertThat(result)
                .extracting("name")
                .contains(book1.getName(), book2.getName(), book3.getName()).size().isEqualTo(3);
    }

    @Test
    void testSearchByNameCaseInsensitive() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setProductName("book");
        ProductSpecification spec = new ProductSpecification(criteria);
        List<Product> result = repo.findAll(spec);
        assertThat(result)
                .extracting("name")
                .contains(book1.getName(), book2.getName(), book3.getName()).size().isEqualTo(3);
    }

    @Test
    void testSearchWithNameAndPrice(){
        SearchCriteria criteria = new SearchCriteria();
        criteria.setProductName("book");
        criteria.setMinPrice(10.25f);
        criteria.setMaxPrice(50.0f);
        ProductSpecification spec = new ProductSpecification(criteria);
        List<Product> result = repo.findAll(spec);
        assertThat(result)
                .extracting("price")
                .contains(10.25f, 30.56f).size().isEqualTo(2);
    }

    @Test
    void testSearchWithAllCriteria(){
        SearchCriteria criteria = new SearchCriteria();
        criteria.setProductName("book");
        criteria.setMinPrice(10.25f);
        criteria.setMaxPrice(600.0f);
        criteria.setMinPostedDate(LocalDate.of(2023, 8, 1));
        criteria.setMaxPostedDate(LocalDate.of(2023, 8, 8));
        ProductSpecification spec = new ProductSpecification(criteria);
        List<Product> result = repo.findAll(spec);
        assertThat(result)
                .extracting("price")
                .contains(10.25f, 30.56f, 50.0f).size().isEqualTo(3);
        assertThat(result)
                .extracting("name")
                .contains(book1.getName(), book2.getName(), book3.getName()).size().isEqualTo(3);
    }

}
