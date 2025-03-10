package com.example.HomeService.service;

import com.example.HomeService.model.ServiceType;
import com.example.HomeService.repo.ServiceTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceTypeService {

    @Autowired
    private ServiceTypeRepository repository;

    @Transactional
    public Long addServiceType(ServiceType serviceName) {
        return repository.findByServiceName(serviceName.getServiceName())
                .map(ServiceType::getServiceId) // Fixed getter method name
                .orElseGet(() -> {
                    ServiceType newService = repository.save(serviceName);
                    return newService.getServiceId();
                });
    }

    // New method to get all service types
    public List<ServiceType> getAllServiceTypes() {
        return repository.findAll();
    }
}
