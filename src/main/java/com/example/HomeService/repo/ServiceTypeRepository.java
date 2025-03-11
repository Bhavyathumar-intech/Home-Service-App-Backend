package com.example.HomeService.repo;

import com.example.HomeService.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    // Find by Name of the service
    Optional<ServiceType> findByServiceName(String serviceName);
}