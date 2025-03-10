package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_type")
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private long serviceId;

    @Column(nullable = false, unique = true)
    private String serviceName;

    public ServiceType(String serviceName) {
        this.serviceName = serviceName;
    }
}
