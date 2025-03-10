package com.example.HomeService.repo;

import com.example.HomeService.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    Optional<ServiceType> findByServiceName(String serviceName); // Fixed method name
}
