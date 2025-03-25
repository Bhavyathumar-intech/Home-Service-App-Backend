package com.example.HomeService.controller;

import com.example.HomeService.dto.serviceProviderDto.ServiceProviderRegisterDto;
import com.example.HomeService.dto.serviceProviderDto.ServiceProviderResponseDto;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.service.ServiceProviderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/service-providers")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    @Autowired
    private HttpServletResponse httpServletResponse;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerServiceProvider(@RequestBody ServiceProviderRegisterDto requestDto) {
        try {
            ResponseEntity<?> registeredProvider = serviceProviderService.registerServiceProvider(requestDto, httpServletResponse);
            return ResponseEntity.ok(registeredProvider);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Retrieves all service providers.
     *
     * @return ResponseEntity containing a list of all service providers.
     */
    @GetMapping
    public ResponseEntity<List<ServiceProvider>> getAllServiceProviders() {
        return ResponseEntity.ok(serviceProviderService.getAllServiceProviders());
    }

    /**
     * Retrieves a service provider by ID.
     *
     * @param id The ID of the service provider.
     * @return ResponseEntity containing the service provider details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceProviderById(@PathVariable Long id) {
        try {
            ServiceProviderResponseDto responseDto = serviceProviderService.getServiceProviderById(id);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    /**
     * Retrieves a service provider by User ID.
     *
     * @param userId The User ID linked to the service provider.
     * @return ResponseEntity containing the service provider details.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getServiceProviderByUserId(@PathVariable Long userId) {
        try {
            ServiceProviderResponseDto responseDto = serviceProviderService.getServiceProviderByUserId(userId);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    /**
     * Retrieves a service provider by Company Name.
     *
     * @param companyName The name of the company.
     * @return ResponseEntity containing the service provider details.
     */
    @GetMapping("/company")
    public ResponseEntity<Optional<ServiceProvider>> getServiceProviderByCompanyName(@RequestParam String companyName) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderByCompanyName(companyName));
    }

    /**
     * Deletes a service provider by serviceProviderId provided in a JSON object.
     *
     * @param requestBody A JSON object containing the serviceProviderId.
     * @return ResponseEntity with a success or error message.
     */
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

    /**
     * Updates an existing service provider.
     *
     * @param serviceProvider The service provider object with updated details.
     * @return ResponseEntity with a success message.
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        System.out.println("Update Accessed");
        serviceProviderService.updateServiceProvider(serviceProvider);
        return ResponseEntity.ok("Service Provider updated successfully");
    }
}
