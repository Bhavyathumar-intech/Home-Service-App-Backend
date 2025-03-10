package com.example.HomeService.service;

import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.model.Users;
import com.example.HomeService.model.Role;
import com.example.HomeService.repo.ServiceProviderRepository;
import com.example.HomeService.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository usersRepository;

    public ServiceProviderService(ServiceProviderRepository serviceProviderRepository, UserRepository usersRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Retrieves all registered service providers.
     * @return List of all service providers.
     */
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }

    /**
     * Registers a new service provider if the user has the PROVIDER role.
     * @param userId The ID of the user registering as a service provider.
     * @param companyName The name of the company.
     * @param experienceYears The number of years of experience.
     * @param address The address of the service provider.
     * @param imageUrl (Optional) The profile image URL.
     * @return The registered service provider entity.
     */
    @Transactional
    public ServiceProvider registerServiceProvider(Long userId, String companyName, int experienceYears, String address, String imageUrl) {
        if (serviceProviderRepository.existsByUserId(userId)) {
            throw new RuntimeException("User is already registered as a service provider");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getRole() != Role.PROVIDER) {
            throw new RuntimeException("User does not have the PROVIDER role");
        }

        // ✅ Now includes `imageUrl`
        ServiceProvider serviceProvider = new ServiceProvider(user, companyName, experienceYears, address, imageUrl);
        return serviceProviderRepository.save(serviceProvider);
    }

    /**
     * Retrieves a service provider by its ID.
     * @param id The ID of the service provider.
     * @return The corresponding service provider entity.
     */
    public ServiceProvider getServiceProviderById(Long id) {
        return serviceProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + id));
    }

    /**
     * Retrieves a service provider by the associated user ID.
     * @param userId The user ID linked to the service provider.
     * @return An optional containing the service provider if found.
     */
    public Optional<ServiceProvider> getServiceProviderByUserId(Long userId) {
        return serviceProviderRepository.findByUserId(userId);
    }

    /**
     * Retrieves a service provider by company name.
     * @param companyName The name of the company.
     * @return An optional containing the service provider if found.
     */
    public Optional<ServiceProvider> getServiceProviderByCompanyName(String companyName) {
        return serviceProviderRepository.findByCompanyName(companyName);
    }

    /**
     * Updates an existing service provider's details.
     * Only the fields that are provided will be updated.
     * @param updatedProvider The service provider object containing updated details.
     */
    @Transactional
    public void updateServiceProvider(ServiceProvider updatedProvider) {
        ServiceProvider existingProvider = serviceProviderRepository.findById(updatedProvider.getServiceProviderId())
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + updatedProvider.getServiceProviderId()));

        // ✅ Update fields if they are provided
        if (updatedProvider.getCompanyName() != null) {
            existingProvider.setCompanyName(updatedProvider.getCompanyName());
        }
        if (updatedProvider.getExperienceYears() > 0) {
            existingProvider.setExperienceYears(updatedProvider.getExperienceYears());
        }
        if (updatedProvider.getAddress() != null) {
            existingProvider.setAddress(updatedProvider.getAddress());
        }
        if (updatedProvider.getPhoneNumber() != null) {
            existingProvider.setPhoneNumber(updatedProvider.getPhoneNumber());
        }
        if (updatedProvider.getImageUrl() != null) {
            existingProvider.setImageUrl(updatedProvider.getImageUrl());
        }

        // ✅ Save the updated provider
        serviceProviderRepository.save(existingProvider);
    }

    /**
     * Calculates the time since the service provider joined.
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
     * @param providerId The ID of the service provider to delete.
     */
    @Transactional
    public void deleteServiceProvider(Long providerId) {
        ServiceProvider serviceProvider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + providerId));

        Users user = serviceProvider.getUser();
        if (user.getRole() != Role.PROVIDER) {
            throw new RuntimeException("User does not have the PROVIDER role and cannot be deleted as a service provider");
        }
        serviceProviderRepository.deleteById(providerId);
    }
}
