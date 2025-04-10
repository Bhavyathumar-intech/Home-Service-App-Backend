package com.example.HomeService.dto.OrdersDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRegisterDto {

    private Long userId;

//    private Long serviceProviderId;
//
//    private Long userDetailsId;

    private Long servicesId;

    private LocalDateTime scheduledDateTime;

    private BigDecimal orderPrice;

    private String paymentMethod;
}
