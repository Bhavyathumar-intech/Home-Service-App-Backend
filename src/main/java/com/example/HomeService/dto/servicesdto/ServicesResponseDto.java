package com.example.HomeService.dto.servicesdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class ServicesResponseDto {

    private Long serviceId;
    private String serviceProviderName;
    private String serviceName;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer expectedDuration;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private boolean status;
    private String imageUrl;

    // Constructor to map entity to DTO
    public ServicesResponseDto(Long serviceId, String serviceProviderName, String serviceName,
                               String description, String category, BigDecimal price, Integer expectedDuration,
                               LocalDate createdAt, LocalDate updatedAt, boolean status, String imageUrl) {
        this.serviceId = serviceId;
        this.serviceProviderName = serviceProviderName;
        this.serviceName = serviceName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.expectedDuration = expectedDuration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.imageUrl = imageUrl;
    }
}

