package com.example.HomeService.service;

import com.example.HomeService.model.ServiceType;
import com.example.HomeService.repo.ServiceTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing Service Types.
 */
@Service
public class ServiceTypeService {

    @Autowired
    private ServiceTypeRepository repository;

    /**
     * Adds a new service type if it does not already exist.
     * If the service type exists, returns its existing ID.
     *
     * @param serviceName The service type to be added.
     * @return The ID of the existing or newly created service type.
     */
    @Transactional
    public Long addServiceType(ServiceType serviceName) {
        return repository.findByServiceName(serviceName.getServiceName())
                .map(ServiceType::getServiceId) // Fixed getter method name
                .orElseGet(() -> {
                    ServiceType newService = repository.save(serviceName);
                    return newService.getServiceId();
                });
    }

    /**
     * Retrieves all service types from the database.
     *
     * @return A list of all service types.
     */
    public List<ServiceType> getAllServiceTypes() {
        return repository.findAll();
    }
}
