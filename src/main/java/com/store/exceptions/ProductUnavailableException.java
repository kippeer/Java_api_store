package com.store.exceptions;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(String productName) {
        super("Product is not available: " + productName);
    }
}
