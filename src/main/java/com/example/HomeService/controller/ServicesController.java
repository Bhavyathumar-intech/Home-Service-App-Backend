package com.example.HomeService.controller;

import com.example.HomeService.dto.servicesDto.ServicesRegisterDto;
import com.example.HomeService.dto.servicesDto.ServicesResponseDto;
import com.example.HomeService.dto.servicesDto.ServicesUpdateDto;
import com.example.HomeService.service.ServicesService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
public class ServicesController {

    private final ServicesService servicesService;
    private final ObjectMapper objectMapper;

    public ServicesController(ServicesService servicesService, ObjectMapper objectMapper) {
        this.servicesService = servicesService;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/register")
    public ResponseEntity<?> createService(
            @RequestPart("ServicesRegisterDto") String servicesRegisterDtoString,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {

        // Convert JSON string to ServiceRegisterDto object
        ServicesRegisterDto servicesRegisterDto = objectMapper.readValue(servicesRegisterDtoString, ServicesRegisterDto.class);

        System.out.println(servicesRegisterDto.toString());

        ResponseEntity<?> createdService = servicesService.createService(servicesRegisterDto, imageFile);
        return createdService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllServices() {
        List<ServicesResponseDto> servicesResponseDtos = servicesService.getAllServices();
        return ResponseEntity.ok().body(servicesResponseDtos);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateService(
            @RequestPart("ServicesUpdateDto") String updatedService,
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            ServicesUpdateDto servicesUpdateDto = objectMapper.readValue(updatedService, ServicesUpdateDto.class);
            Long id = servicesUpdateDto.getServiceId();
            ResponseEntity<ServicesResponseDto> service = servicesService.updateService(id, servicesUpdateDto, imageFile);
            return service;
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    // Send that uuid of image on this route to get Image
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\ServicesImage\\" + filename);
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
