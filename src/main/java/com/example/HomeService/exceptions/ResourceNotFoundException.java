package com.example.HomeService.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found.");
    }
}

