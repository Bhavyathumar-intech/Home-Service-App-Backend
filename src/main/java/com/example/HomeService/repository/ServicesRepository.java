package com.example.HomeService.repository;

import com.example.HomeService.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicesRepository extends JpaRepository<Services, Long> {

    // Find all services by category
    Optional<Services> findByCategory(String category);

    // Find services by provider ID
    Optional<List<Services>> findByServiceProvider_ServiceProviderId(Long serviceProviderId);


    // Find services that are available (status = true)
    Optional<Services> findByStatusTrue();

//    // Delete service based on ServiceProvider ID and Service Name
//    void deleteByserviceProviderIdAndserviceName(Long serviceProviderId, String serviceName);
//
//    // Check if service exists by ServiceProvider ID and Service Name
//    boolean existsByserviceProviderIdAndserviceName(Long serviceProviderId, String serviceName);

}
