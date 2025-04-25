package com.example.HomeService.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

    // Method Overloading for handling various exceptions

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found.");
    }
    public ResourceNotFoundException(String resource, HttpStatus status) {
        super(resource + " with Error Code " + status);
    }
    public ResourceNotFoundException(String resource, String value) {
        super(resource + " with value '" + value + "' not found.");
    }
    public ResourceNotFoundException(String resource) {
        super(resource  + " not found.");
    }
}

