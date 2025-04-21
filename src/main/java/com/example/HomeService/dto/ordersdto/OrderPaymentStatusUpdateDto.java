package com.example.HomeService.dto.ordersdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentStatusUpdateDto {
    private Long orderId;
    private Long serviceProviderId;
    private String paymentStatus; // "PAID" or other statuses like "PENDING" etc.
}
