package com.example.HomeService.repository;

import com.example.HomeService.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    // Find provider by user ID
    Optional<ServiceProvider> findByUserId(Long userId);

    // Find provider by company name
    Optional<ServiceProvider> findByCompanyName(String companyName);

    // Check if provider exists for a given user
    boolean existsByUserId(Long userId);
}
