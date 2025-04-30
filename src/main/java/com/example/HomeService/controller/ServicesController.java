package com.example.HomeService.controller;

import com.example.HomeService.dto.servicesdto.ServicesRegisterDto;
import com.example.HomeService.dto.servicesdto.ServicesResponseDto;
import com.example.HomeService.dto.servicesdto.ServicesUpdateDto;
import com.example.HomeService.service.ServicesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
@PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
public class ServicesController {

    private final ServicesService servicesService;
    private final ObjectMapper objectMapper;

    public ServicesController(ServicesService servicesService, ObjectMapper objectMapper) {
        this.servicesService = servicesService;
        this.objectMapper = objectMapper;
    }

    /**
     * Registers a new service along with an image file.
     *
     * @param servicesRegisterDtoString JSON string containing service registration details.
     * @param imageFile                 The image file associated with the service.
     * @return ResponseEntity with the created service or error.
     * @throws IOException if there is an error parsing the JSON or handling the image.
     */
    @PostMapping("/register")
    public ResponseEntity<?> createService(
            @RequestPart("ServicesRegisterDto") String servicesRegisterDtoString,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        if (servicesRegisterDtoString == null || servicesRegisterDtoString.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Service registration data cannot be null or empty.");
        }

        try {
            ServicesRegisterDto servicesRegisterDto = objectMapper.readValue(servicesRegisterDtoString, ServicesRegisterDto.class);
            return servicesService.createService(servicesRegisterDto, imageFile);

        } catch (JsonMappingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format or structure in service registration data.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error processing JSON.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("I/O error occurred while processing the request.");
        } catch (Exception e) {
            // Catch-all for unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Fetches all services available for users.
     *
     * @return List of ServicesResponseDto containing service details.
     */
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllServices() {
        List<ServicesResponseDto> servicesResponseDtos = servicesService.getAllServices();
        return ResponseEntity.ok().body(servicesResponseDtos);
    }

    /**
     * Updates an existing service's details and optionally its image.
     *
     * @param updatedService JSON string containing updated service details.
     * @param imageFile      Optional image file to update.
     * @return Updated service details or error if not found.
     */
    @PatchMapping("/update")
    public ResponseEntity<?> updateService(
            @RequestPart("ServicesUpdateDto") String updatedService,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        if (updatedService == null) {
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }

        try {
            ServicesUpdateDto servicesUpdateDto = objectMapper.readValue(updatedService, ServicesUpdateDto.class);
            Long id = servicesUpdateDto.getServiceId();
            return servicesService.updateService(id, servicesUpdateDto, imageFile);
        } catch (JsonMappingException e) {
            // JSON is badly formatted or doesn't match the DTO
            return ResponseEntity.badRequest().body("Invalid JSON format or structure.");
        } catch (JsonProcessingException e) {
            // General JSON processing error
            return ResponseEntity.badRequest().body("Error processing JSON.");
        } catch (EntityNotFoundException e) {
            // Custom or JPA exception indicating the service with the given ID doesn't exist
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            // I/O error (maybe reading from the input stream failed)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("I/O error occurred.");
        }
    }

    /**
     * Fetches services offered by a specific service provider.
     *
     * @param providerId ID of the service provider whose services are being requested.
     * @return List of services provided by the specified provider.
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getServicesByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(servicesService.getServicesByServiceProviderId(providerId));
    }

    //    @DeleteMapping("/{id}")
    //    public ResponseEntity<?> deleteService(@PathVariable Long id) {
    //        try {
    //            servicesService.deleteService(id);  // Try to delete the service
    //            return ResponseEntity.ok("Service with ID " + id + " deleted successfully.");  // Return 200 with a success message
    //        } catch (RuntimeException e) {
    //            // Catch the exception thrown when service is not found and return 404
    //            if (e.getMessage().contains("Service not found")) {
    //                return ResponseEntity.notFound().build();  // Return 404 if service is not found
    //            }
    //            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");  // Return 500 if any unexpected error occurs
    //        }
    //    }

    /**
     * Deletes a service by its ID.
     *
     * @param id ID of the service to be deleted.
     * @return A success or error message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteService(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            servicesService.deleteService(id);
            response.put("success", "Service with ID " + id + " deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Service not found")) {
                response.put("fail", "Service with ID " + id + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("fail", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Serves an image associated with a service by filename.
     *
     * @param filename The filename of the image to be served.
     * @return The image file or a 404 response if not found.
     * @throws MalformedURLException if the path to the image is incorrect.
     */
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\ServicesImage\\" + filename);
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
