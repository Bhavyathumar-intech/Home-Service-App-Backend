package com.example.HomeService.dto.ordersdto;

import com.example.HomeService.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;

    private Long customerId;
    private String customerName;
    private String customerNumber;

    private Long serviceProviderId;
    private String serviceProviderName;
    private String serviceProviderNumber;

    private Long userDetailsId;
    private String address;

    private Long serviceId;
    private String serviceName;

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private OrderStatus status;

    private BigDecimal orderPrice;
    private String paymentMethod;

    private LocalDate orderedAt;
    private LocalDate updatedAt;
}