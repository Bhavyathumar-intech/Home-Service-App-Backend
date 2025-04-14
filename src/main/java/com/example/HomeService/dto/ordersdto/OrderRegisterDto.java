package com.example.HomeService.dto.ordersdto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegisterDto {

    private Long userId;

    private Long servicesId;

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private BigDecimal orderPrice;

    private String paymentMethod;
}
