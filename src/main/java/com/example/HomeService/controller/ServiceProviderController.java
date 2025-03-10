package com.example.HomeService.controller;

import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.service.ServiceProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/service-providers")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    // ✅ Updated: Register a new service provider with optional `imageUrl`
    @PostMapping("/register")
    public ResponseEntity<?> registerServiceProvider(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.valueOf(requestData.get("userId").toString());
            String companyName = requestData.get("companyName").toString();
            int experienceYears = Integer.parseInt(requestData.get("experienceYears").toString());
            String address = requestData.get("address").toString();

            // ✅ Check if `imageUrl` is provided
            String imageUrl = requestData.containsKey("imageUrl") ? requestData.get("imageUrl").toString() : null;

            ServiceProvider registeredProvider = serviceProviderService.registerServiceProvider(
                    userId, companyName, experienceYears, address, imageUrl
            );

            return ResponseEntity.ok(registeredProvider);

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

//    @PostMapping
//    public ResponseEntity<ServiceProvider> registerServiceProvider(@RequestBody ServiceProvider serviceProvider) {
//        ServiceProvider createdProvider = serviceProviderService.registerServiceProvider(serviceProvider);
//        return ResponseEntity.ok(createdProvider);
//    }


    // Get all service providers
    @GetMapping
    public ResponseEntity<List<ServiceProvider>> getAllServiceProviders() {
        return ResponseEntity.ok(serviceProviderService.getAllServiceProviders());
    }

    // Get service provider by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceProvider> getServiceProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderById(id));
    }

    // Get service provider by User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<ServiceProvider>> getServiceProviderByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderByUserId(userId));
    }

    // Get service provider by Company Name
    @GetMapping("/company")
    public ResponseEntity<Optional<ServiceProvider>> getServiceProviderByCompanyName(@RequestParam String companyName) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderByCompanyName(companyName));
    }

    // Get time since provider joined
    @GetMapping("/{id}/time-since-joined")
    public ResponseEntity<String> getTimeSinceJoined(@PathVariable Long id) {
        return ResponseEntity.ok(serviceProviderService.getTimeSinceJoined(id));
    }

    // FrontEnd will send ProviderID from JwtToken for Delete request
    // Delete a service provider by serviceProviderId in Json Obj format
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteServiceProvider(@RequestBody Map<String, Long> requestBody) {
        Long providerId = requestBody.get("serviceProviderId"); // Extracting 'serviceProviderId' instead of 'providerId'

        if (providerId == null) {
            return ResponseEntity.badRequest().body("serviceProviderId is required");
        }

        System.out.println("Delete Accessed with ID: " + providerId);
        serviceProviderService.deleteServiceProvider(providerId);

        return ResponseEntity.ok("Service Provider deleted successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        System.out.println("Update Accessed");
        serviceProviderService.updateServiceProvider(serviceProvider);
        return ResponseEntity.ok("Service Provider updated successfully");
    }
}