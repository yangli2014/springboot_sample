package com.yang.product.product.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String reason;

    @Column(name = "request_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime requestTime;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    private float newPrice;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    public Approval() {
        requestTime = LocalDateTime.now();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        this.newPrice = newPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }
}
