package com.jpm.stockmanagement.exceptions;

public class FetchStockDetailsException extends RuntimeException {
    public FetchStockDetailsException(String message) {
        super(message);
    }
}
