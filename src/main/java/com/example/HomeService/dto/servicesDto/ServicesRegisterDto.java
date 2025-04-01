package com.example.HomeService.dto.servicesDto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class ServicesRegisterDto {

    private Long serviceProvider;
    private String serviceName;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer expectedDuration;
    private boolean status;
    private String imageUrl;

    public ServicesRegisterDto(Long serviceProvider, String serviceName, String description, String category, BigDecimal price, Integer expectedDuration, boolean status, String imageUrl) {
        this.serviceProvider = serviceProvider;
        this.serviceName = serviceName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.expectedDuration = expectedDuration;
        this.status = status;
        this.imageUrl = imageUrl;
    }
}



