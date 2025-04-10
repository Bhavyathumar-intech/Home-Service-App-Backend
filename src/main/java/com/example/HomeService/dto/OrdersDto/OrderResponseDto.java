package com.example.HomeService.dto.OrdersDto;

import com.example.HomeService.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Long userDetailsId;
    private String address;

    private Long serviceId;
    private String serviceName;

    private LocalDateTime scheduledDateTime;

    private OrderStatus status;

    private BigDecimal orderPrice;
    private String paymentMethod;

    private LocalDate orderedAt;
    private LocalDate updatedAt;
}