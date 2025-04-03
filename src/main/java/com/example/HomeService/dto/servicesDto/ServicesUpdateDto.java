package com.example.HomeService.dto.servicesDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ServicesUpdateDto {

    private Long serviceId;
    private String serviceName;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer expectedDuration;
    private boolean status;
    private String imageUrl;

    // Constructor to map entity to DTO
    public ServicesUpdateDto(Long serviceId, String serviceName,String description, String category, BigDecimal price, Integer expectedDuration,
                             boolean status, String imageUrl)
    {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.expectedDuration = expectedDuration;
        this.status = status;
        this.imageUrl = imageUrl;
    }
}
