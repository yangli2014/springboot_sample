package com.yang.product.product.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;
    private float price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status;

    @Column(name = "post_date", columnDefinition = "TIMESTAMP")
    private LocalDate postDate;

    public Product() {
        postDate = LocalDate.now();
    }

    public Product(String name, float price, ProductStatus productStatus) {
        this.name = name;
        this.price = price;
        this.status = productStatus;
        postDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus productStatus) {
        this.status = productStatus;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }
}
