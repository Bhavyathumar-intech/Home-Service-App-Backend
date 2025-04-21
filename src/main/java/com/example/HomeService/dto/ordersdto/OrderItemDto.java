package com.example.HomeService.dto.ordersdto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Long serviceId;
    private String serviceName;
    private int quantity;
}