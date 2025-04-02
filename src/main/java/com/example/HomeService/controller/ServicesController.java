package com.example.HomeService.controller;

import com.example.HomeService.dto.servicesDto.ServicesRegisterDto;
import com.example.HomeService.dto.servicesDto.ServicesResponseDto;
import com.example.HomeService.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
public class ServicesController {

    @Autowired
    private ServicesService servicesService;

    @PostMapping("/register")
    public ResponseEntity<?> createService(@RequestBody ServicesRegisterDto servicesRegisterDto, MultipartFile imageFile) throws IOException {
        // Convert DTO to model object
        System.out.println(servicesRegisterDto.toString());
        ResponseEntity<?> createdService = servicesService.createService(servicesRegisterDto, imageFile);
        return createdService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllServices() {
        List<ServicesResponseDto> servicesResponseDtos = servicesService.getAllServices();
        return ResponseEntity.ok().body( servicesResponseDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServicesRegisterDto updatedService) {
        try {
            ResponseEntity<ServicesResponseDto> service = servicesService.updateService(id, updatedService);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get Based on ServiceProviders
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getServicesByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(servicesService.getServicesByServiceProviderId(providerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        try {
            servicesService.deleteService(id);  // Try to delete the service
            return ResponseEntity.ok("Service with ID " + id + " deleted successfully.");  // Return 200 with a success message
        } catch (RuntimeException e) {
            // Catch the exception thrown when service is not found and return 404
            if (e.getMessage().contains("Service not found")) {
                return ResponseEntity.notFound().build();  // Return 404 if service is not found
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");  // Return 500 if any unexpected error occurs
        }
    }

}
