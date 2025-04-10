package com.example.HomeService.dto.OrdersDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusResponse {
    private String message;
    private String status;

    public UpdateOrderStatusResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
