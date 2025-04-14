package com.example.HomeService.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found.");
    }
    public ResourceNotFoundException(String resource, String value) {
        super(resource + " with value '" + value + "' not found.");
    }
    public ResourceNotFoundException(String resource) {
        super(resource  + " not found.");
    }
}

