package com.yang.product.product.exception;

import org.springframework.http.HttpStatus;

public class ApprovalException extends ProductException {
    public ApprovalException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
