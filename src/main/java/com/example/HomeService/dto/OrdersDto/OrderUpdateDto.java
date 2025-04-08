package com.example.HomeService.dto.OrdersDto;

import com.example.HomeService.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateDto {

    private Long orderId; // Required to identify the order

    private LocalDateTime scheduledDateTime;

    private OrderStatus status;

    private String paymentMethod;
}

