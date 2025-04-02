package com.example.HomeService.service;

import com.example.HomeService.dto.servicesDto.ServicesResponseDto;
import com.example.HomeService.dto.servicesDto.ServicesRegisterDto;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.model.Services;
import com.example.HomeService.repo.ServiceProviderRepository;
import com.example.HomeService.repo.ServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServicesService {

    private final ServicesRepository serviceRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private static final String IMAGE_DIRECTORY = "D:\\Project\\Home-Service-App-Backend\\src\\ServicesImage";

    @Autowired
    public ServicesService(ServicesRepository serviceRepository, ServiceProviderRepository serviceProviderRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceProviderRepository = serviceProviderRepository;
    }

    private String storeImage(MultipartFile file) throws IOException {
        // Ensure directory exists
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_DIRECTORY, fileName);
        // Save file to disk
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    // Convert Services to ServiceResponseDTO
    private ServicesResponseDto convertToDTO(Services service) {
        return new ServicesResponseDto(
                service.getServiceId(),
                service.getServiceProvider().getCompanyName(),
                service.getServiceName(),
                service.getDescription(),
                service.getCategory(),
                service.getPrice(),
                service.getExpectedDuration(),
                service.getCreatedAt(),
                service.getUpdatedAt(),
                service.isStatus(),
                service.getImage_url()
        );
    }

    public ResponseEntity<?> createService(ServicesRegisterDto dto, MultipartFile imageFile) throws IOException {
        // Check if a service with the same serviceName and serviceProvider already exists in the database
        Optional<Services> existingService = serviceRepository.findAll().stream()
                .filter(serv -> serv.getServiceName().equals(dto.getServiceName()) &&
                        serv.getServiceProvider().getServiceProviderId().equals(dto.getServiceProvider()))
                .findFirst();

        // If a service exists with the same serviceName and serviceProvider, throw an exception
        if (existingService.isPresent()) {
            return ResponseEntity.badRequest().body("Service with the same name already exists for this provider.");
        }

        Services service = new Services();
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProvider()).orElseThrow(() -> new RuntimeException("Service provider not found"));
        service.setServiceProvider(serviceProvider);
        service.setServiceName(dto.getServiceName());
        service.setDescription(dto.getDescription());
        service.setCategory(dto.getCategory());
        service.setPrice(dto.getPrice());
        service.setExpectedDuration(dto.getExpectedDuration());
        service.setStatus(dto.isStatus());
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeImage(imageFile);
            service.setImage_url(fileName);
        }
//        service.setImage_url(dto.getImageUrl());

        // Save service and return response as DTO
        Services savedService = serviceRepository.save(service);
        return ResponseEntity.ok(convertToDTO(savedService));
    }

    public List<ServicesResponseDto> getAllServices() {
        // Convert all services to DTOs before returning
        return serviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ServicesResponseDto> getServiceById(Long id) {
        Optional<Services> service = serviceRepository.findById(id);
        return service.map(this::convertToDTO);
    }

    public ResponseEntity<ServicesResponseDto> updateService(Long id, ServicesRegisterDto dto) {
        Services service = serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found"));
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProvider()).orElseThrow(() -> new RuntimeException("Service provider not found"));

        service.setServiceProvider(serviceProvider);
        service.setServiceName(dto.getServiceName());
        service.setDescription(dto.getDescription());
        service.setCategory(dto.getCategory());
        service.setPrice(dto.getPrice());
        service.setExpectedDuration(dto.getExpectedDuration());
        service.setStatus(dto.isStatus());
        service.setImage_url(dto.getImageUrl());
        service.setUpdatedAt(LocalDate.now());

        Services updatedService = serviceRepository.save(service);
        return ResponseEntity.ok(convertToDTO(updatedService));
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found with ID: " + id);
        }
        serviceRepository.deleteById(id);
    }

    public List<ServicesResponseDto> getServicesByServiceProviderId(Long serviceProviderId) {
        List<Services> services = serviceRepository.findByServiceProvider_ServiceProviderId(serviceProviderId).orElseThrow(() -> new RuntimeException("No services found for provider"));
        return services.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
