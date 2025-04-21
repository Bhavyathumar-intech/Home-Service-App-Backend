package com.example.HomeService.dto.ordersdto;

import com.example.HomeService.model.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateDto {

    private Long orderId; // Required to identify the order

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

}

