package com.store.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName) {
        super("Not enough stock for product: " + productName);
    }
}
