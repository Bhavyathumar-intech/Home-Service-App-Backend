package com.example.HomeService.dto.ordersdto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegisterDto {

    private Long userId;
    private Long serviceProviderId;

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private String paymentMethod; // "COD" or "ONLINE"

    private List<OrderItemDto> items;
}
