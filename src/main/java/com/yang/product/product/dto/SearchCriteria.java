package com.yang.product.product.dto;

import java.time.LocalDate;

public class SearchCriteria {
    private String productName;
    private Float minPrice;
    private Float maxPrice;
    private LocalDate minPostedDate;
    private LocalDate maxPostedDate;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDate getMinPostedDate() {
        return minPostedDate;
    }

    public void setMinPostedDate(LocalDate minPostedDate) {
        this.minPostedDate = minPostedDate;
    }

    public LocalDate getMaxPostedDate() {
        return maxPostedDate;
    }

    public void setMaxPostedDate(LocalDate maxPostedDate) {
        this.maxPostedDate = maxPostedDate;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
                "productName='" + productName + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minPostedDate=" + minPostedDate +
                ", maxPostedDate=" + maxPostedDate +
                '}';
    }
}
