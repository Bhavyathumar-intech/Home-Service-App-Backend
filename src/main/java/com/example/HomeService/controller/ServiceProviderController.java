package com.example.HomeService.controller;

import com.example.HomeService.dto.serviceProviderDto.ServiceProviderRegisterDto;
import com.example.HomeService.dto.serviceProviderDto.ServiceProviderResponseDto;
import com.example.HomeService.dto.serviceProviderDto.ServiceProviderUpdateDto;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.service.ServiceProviderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public ServiceProviderController(ServiceProviderService serviceProviderService)
    {
        this.serviceProviderService = serviceProviderService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerServiceProvider(
            @RequestPart("ServiceProviderRegisterDto") String requestDtoString,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException
    {
        try {
            System.out.println(requestDtoString);
            // Convert JSON string to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceProviderRegisterDto requestDto = objectMapper.readValue(requestDtoString, ServiceProviderRegisterDto.class);

            System.out.println(requestDto);


            // Call service method
            ResponseEntity<?> registeredProvider = serviceProviderService.registerServiceProvider(requestDto, httpServletResponse, imageFile);

            return ResponseEntity.ok(registeredProvider);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JSON format"));
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
    public ResponseEntity<ServiceProviderResponseDto> updateServiceProvider(
            @RequestPart("ServiceProviderUpdateDto") String serviceProvider ,
            @RequestPart("imageFile") MultipartFile imageFile ) {

        try {
            // Convert JSON string to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceProviderUpdateDto requestDto = objectMapper.readValue(serviceProvider, ServiceProviderUpdateDto.class);

            System.out.println("adnfbdfbhusdbfuhsdbfu tnsdftu Vfvtq VF"+ requestDto);


            // Call service method
            ResponseEntity<ServiceProviderResponseDto> registeredProvider = serviceProviderService.updateServiceProvider(requestDto,imageFile);

            return registeredProvider;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Send that uuid of image on this route to get Image
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\ServiceProviderImage\\" + filename);
        Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Change if using PNG, etc.
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
