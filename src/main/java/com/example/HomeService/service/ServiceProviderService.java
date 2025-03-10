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

    // ✅ Get all service providers
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }

    //     ✅ Register a new service provider (only if user role is PROVIDER) with imageUrl
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

    // ✅ Get a service provider by ID
    public ServiceProvider getServiceProviderById(Long id) {
        return serviceProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + id));
    }

    // ✅ Get a service provider by User ID
    public Optional<ServiceProvider> getServiceProviderByUserId(Long userId) {
        return serviceProviderRepository.findByUserId(userId);
    }

    // ✅ Get a service provider by Company Name
    public Optional<ServiceProvider> getServiceProviderByCompanyName(String companyName) {
        return serviceProviderRepository.findByCompanyName(companyName);
    }

    // ✅ Update a service provider (only fields that are provided will be updated)
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

    //Calculate time since provider joined
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

    // ✅ Delete a service provider
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
