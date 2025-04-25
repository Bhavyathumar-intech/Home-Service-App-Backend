package com.example.HomeService.exceptions;

public class PaymentUpdateNotAllowedException extends RuntimeException {

    public PaymentUpdateNotAllowedException(String message) {
        super(message);
    }
}

