package com.yang.product.product.dto;

import com.yang.product.product.entity.Approval;

import java.time.LocalDateTime;

public class ApprovalDTO {
    private long id;
    private String reason;
    private LocalDateTime requestTime;
    private long productId;

    public ApprovalDTO() {
    }

    public ApprovalDTO(Approval entity) {
        id = entity.getId();
        reason = entity.getReason();
        requestTime = entity.getRequestTime();
        productId = entity.getProduct().getId();
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

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
