package com.example.HomeService.controller;

import com.example.HomeService.model.ServiceType;
import com.example.HomeService.service.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling service type-related API endpoints.
 */
@CrossOrigin
@RestController
@RequestMapping("/service-types")
public class ServiceTypeController {

    @Autowired
    private ServiceTypeService serviceTypeService;

    /**
     * Endpoint to add a new service type.
     *
     * @param serviceName The service type to be added.
     * @return The ID of the newly created or existing service type.
     */
    @PostMapping
    public ResponseEntity<Long> addServiceType(@RequestBody ServiceType serviceName) {
        Long id = serviceTypeService.addServiceType(serviceName);
        return ResponseEntity.ok(id);
    }

    /**
     * Endpoint to retrieve all service types.
     *
     * @return A list of all available service types.
     */
    @GetMapping
    public ResponseEntity<List<ServiceType>> getAllServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeService.getAllServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }
}
