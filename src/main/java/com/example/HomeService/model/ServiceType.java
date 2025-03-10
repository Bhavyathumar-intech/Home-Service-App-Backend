package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a type of service.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_type")
public class ServiceType {

    /**
     * Unique identifier for the service type.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private long serviceId;

    /**
     * Name of the service type, must be unique and not null.
     */
    @Column(nullable = false, unique = true)
    private String serviceName;

    /**
     * Constructor to create a ServiceType with a given name.
     *
     * @param serviceName The name of the service type.
     */
    public ServiceType(String serviceName) {
        this.serviceName = serviceName;
    }
}