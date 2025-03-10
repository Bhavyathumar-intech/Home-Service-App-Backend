package com.example.HomeService.controller;

import com.example.HomeService.model.ServiceType;
import com.example.HomeService.service.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/service-types")
public class ServiceTypeController {

    @Autowired
    private ServiceTypeService serviceTypeService;

    // Add a new service type
    @PostMapping
    public ResponseEntity<Long> addServiceType(@RequestBody ServiceType serviceName) {
        Long id = serviceTypeService.addServiceType(serviceName);
        return ResponseEntity.ok(id);
    }

    // Retrieve all service categories
    @GetMapping
    public ResponseEntity<List<ServiceType>> getAllServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeService.getAllServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }
}
