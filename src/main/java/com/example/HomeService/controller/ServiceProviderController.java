package com.example.HomeService.controller;

import com.example.HomeService.dto.serviceproviderdto.ServiceProviderRegisterDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderResponseDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderUpdateDto;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.service.ServiceProviderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyRole('PROVIDER')")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    private final HttpServletResponse httpServletResponse;

    public ServiceProviderController(ServiceProviderService serviceProviderService, HttpServletResponse httpServletResponse) {
        this.serviceProviderService = serviceProviderService;
        this.httpServletResponse = httpServletResponse;
    }

    /**
     * Registers a new service provider with profile image.
     *
     * @param requestDtoString JSON string representing ServiceProviderRegisterDto.
     * @param imageFile        Multipart image file of the service provider.
     * @return ResponseEntity with registration status.
     * @throws IOException if image processing or JSON parsing fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerServiceProvider(
            @RequestPart("ServiceProviderRegisterDto") String requestDtoString,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {
        try {
            System.out.println(requestDtoString);
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceProviderRegisterDto requestDto = objectMapper.readValue(requestDtoString, ServiceProviderRegisterDto.class);

            return serviceProviderService.registerServiceProvider(requestDto, httpServletResponse, imageFile);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid JSON format"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Retrieves a list of all registered service providers.
     *
     * @return List of ServiceProvider objects.
     */
    @GetMapping
    public ResponseEntity<List<ServiceProvider>> getAllServiceProviders() {
        return ResponseEntity.ok(serviceProviderService.getAllServiceProviders());
    }

    /**
     * Retrieves a service provider by its ID.
     *
     * @param id Service provider ID.
     * @return ServiceProviderResponseDto if found, otherwise an error message.
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
     * Retrieves a service provider by the associated user ID.
     *
     * @param userId User ID linked to the service provider.
     * @return ServiceProviderResponseDto if found, otherwise an error message.
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
     * Retrieves a service provider by company name.
     *
     * @param companyName Name of the company.
     * @return Optional containing the ServiceProvider if found.
     */
    @GetMapping("/company")
    public ResponseEntity<Optional<ServiceProvider>> getServiceProviderByCompanyName(@RequestParam String companyName) {
        return ResponseEntity.ok(serviceProviderService.getServiceProviderByCompanyName(companyName));
    }

    /**
     * Deletes a service provider based on the provided ID.
     *
     * @param requestBody Map containing the serviceProviderId.
     * @return ResponseEntity with deletion status.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteServiceProvider(@RequestBody Map<String, Long> requestBody) {
        Long providerId = requestBody.get("serviceProviderId");

        if (providerId == null) {
            return ResponseEntity.badRequest().body("serviceProviderId is required");
        }

        System.out.println("Delete Accessed with ID: " + providerId);
        return serviceProviderService.deleteServiceProvider(providerId);
    }

    /**
     * Updates a service provider's information and optionally updates the profile image.
     *
     * @param serviceProvider JSON string representing ServiceProviderUpdateDto.
     * @param imageFile       Optional image file to update.
     * @return Updated ServiceProviderResponseDto.
     */
    @PatchMapping("/update")
    public ResponseEntity<ServiceProviderResponseDto> updateServiceProvider(
            @RequestPart("ServiceProviderUpdateDto") String serviceProvider,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceProviderUpdateDto requestDto = objectMapper.readValue(serviceProvider, ServiceProviderUpdateDto.class);

            return serviceProviderService.updateServiceProvider(requestDto, imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an image file by filename.
     *
     * @param filename Name of the image file.
     * @return Resource containing the image data with correct media type.
     * @throws MalformedURLException if the file path is invalid.
     */
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\ServiceProviderImage\\" + filename);
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        MediaType mediaType;
        switch (fileExtension) {
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "bmp":
                mediaType = MediaType.parseMediaType("image/bmp");
                break;
            case "webp":
                mediaType = MediaType.parseMediaType("image/webp");
                break;
            case "jpg":
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
