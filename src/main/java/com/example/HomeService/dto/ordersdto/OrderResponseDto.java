package com.example.HomeService.dto.ordersdto;

import com.example.HomeService.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private OrderStatus status;

    private String paymentMethod;
    private String paymentStatus;

    private List<OrderItemDto> items; // Changed here

    private BigDecimal totalAmount; // Optional: calculated from items

    private LocalDate orderedAt;
    private LocalDate updatedAt;
}
