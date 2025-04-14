package com.example.HomeService.service;

import com.example.HomeService.dto.serviceproviderdto.ServiceProviderRegisterDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderResponseDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderUpdateDto;
import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.model.Users;
import com.example.HomeService.model.Role;
import com.example.HomeService.repository.ServiceProviderRepository;
import com.example.HomeService.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository usersRepository;
    @Autowired
    private JWTservice jwtService;

    private static final String IMAGE_DIRECTORY = "D:\\Project\\Home-Service-App-Backend\\src\\ServiceProviderImage";

    public ServiceProviderService(ServiceProviderRepository serviceProviderRepository, UserRepository usersRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.usersRepository = usersRepository;
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

    /**
     * Retrieves all registered service providers.
     *
     * @return List of all service providers.
     */
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> registerServiceProvider(ServiceProviderRegisterDto requestDto, HttpServletResponse response, MultipartFile imageFile) throws IOException {
        if (serviceProviderRepository.existsByUserId(requestDto.getUserId())) {
            throw new ResourceNotFoundException("User is already registered as a service provider", requestDto.getUserId());
        }

        Users user = usersRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: ", requestDto.getUserId()));

        if (user.getRole() != Role.PROVIDER) {
            throw new ResourceNotFoundException("User does not have the PROVIDER role");
        }

        //  Save service provider in DB
        ServiceProvider serviceProvider = new ServiceProvider(
                user,
                requestDto.getCompanyName(),
                requestDto.getExperienceYears(),
                requestDto.getAddress(),
                requestDto.getCompanyNumber()
        );
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeImage(imageFile);
            serviceProvider.setImageUrl(fileName);
        }
        serviceProvider = serviceProviderRepository.save(serviceProvider); // Save to DB

        Long serviceProviderId = serviceProvider.getServiceProviderId(); //  Get newly generated ID

        //  Generate a NEW JWT token with serviceProviderId now included
        String newJwtToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().toString(),
                user.getId(),
                serviceProviderId //  Now it includes the generated ID
        );

        //  Update the auth cookie with the new token
        ResponseCookie updatedCookie = ResponseCookie.from("authToken", newJwtToken)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .sameSite("Lax")  //  Prevent CSRF attacks
                .build();
        response.addHeader("Set-Cookie", updatedCookie.toString());

        //  Use DTO Constructor Instead of Manual Mapping
        ServiceProviderResponseDto responseDto = new ServiceProviderResponseDto(serviceProvider);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Registration successful");
        responseBody.put("serviceProvider", responseDto);
        responseBody.put("token", newJwtToken);

        return ResponseEntity.ok(responseBody);
    }


    /**
     * Retrieves a service provider by its ID.
     *
     * @param id The ID of the service provider.
     * @return The corresponding service provider entity.
     */

    public ServiceProviderResponseDto getServiceProviderById(Long id) {
        ServiceProvider serviceProvider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with ID: " + id));

        return new ServiceProviderResponseDto(serviceProvider); //  Using your existing DTO
    }


    /**
     * Retrieves a service provider by the associated user ID.
     *
     * @param userId The user ID linked to the service provider.
     * @return An optional containing the service provider if found.
     */

    public ServiceProviderResponseDto getServiceProviderByUserId(Long userId) {
        ServiceProvider serviceProvider = serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found for user ID: " + userId));

        return new ServiceProviderResponseDto(serviceProvider);
    }


    /**
     * Retrieves a service provider by company name.
     *
     * @param companyName The name of the company.
     * @return An optional containing the service provider if found.
     */
    public Optional<ServiceProvider> getServiceProviderByCompanyName(String companyName) {
        return serviceProviderRepository.findByCompanyName(companyName);
    }

    /**
     * Updates an existing service provider's details.
     * Only the fields that are provided will be updated.
     *
     * @param updatedProvider The service provider object containing updated details.
     */
    @Transactional
    public ResponseEntity<ServiceProviderResponseDto> updateServiceProvider(ServiceProviderUpdateDto updatedProvider, MultipartFile imageFile) throws IOException {
        ServiceProvider existingProvider = serviceProviderRepository.findById(updatedProvider.getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with ID: " + updatedProvider.getServiceProviderId()));

        //  Update fields if they are provided
        if (updatedProvider.getCompanyName() != null) {
            existingProvider.setCompanyName(updatedProvider.getCompanyName());
        }
        if (updatedProvider.getExperienceYears() > 0) {
            existingProvider.setExperienceYears(updatedProvider.getExperienceYears());
        }
        if (updatedProvider.getAddress() != null) {
            existingProvider.setAddress(updatedProvider.getAddress());
        }
        if (updatedProvider.getCompanyNumber() != null) {
            existingProvider.setCompanyNumber(updatedProvider.getCompanyNumber());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeImage(imageFile);
            existingProvider.setImageUrl(fileName);
        }

        ServiceProvider serviceProvider = serviceProviderRepository.save(existingProvider);

        //  Save the updated provider
        return ResponseEntity.ok(new ServiceProviderResponseDto(serviceProvider));
    }

    /**
     * Calculates the time since the service provider joined.
     *
     * @param providerId The ID of the service provider.
     * @return A string representing the time since joining in years, months, and days.
     */
    public String getTimeSinceJoined(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + providerId));

        LocalDate joiningDate = provider.getJoiningDate();
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(joiningDate, currentDate);

        return period.getYears() + " years, " +
                period.getMonths() + " months, " +
                period.getDays() + " days";
    }

    /**
     * Deletes a service provider from the system.
     *
     * @param providerId The ID of the service provider to delete.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> deleteServiceProvider(Long providerId) {
        Map<String, String> response = new HashMap<>();

        try {
            ServiceProvider serviceProvider = serviceProviderRepository.findById(providerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with ID: ", providerId));

            Users user = serviceProvider.getUser();
            if (user.getRole() != Role.PROVIDER) {
                throw new ResourceNotFoundException("User does not have the PROVIDER role and cannot be deleted as a service provider");
            }

            serviceProviderRepository.deleteById(providerId);
            response.put("success", "Service provider deleted successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("fail", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
