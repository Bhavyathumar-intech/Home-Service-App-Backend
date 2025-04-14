package com.example.HomeService.dto.ordersdto;

import com.example.HomeService.model.OrderStatus;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEmailDto {

    private Long orderId;

    private String customerName;
    private String customerNumber;

    private String serviceProviderName;
    private String serviceProviderCompanyNumber;

    private String address;

    private String serviceName;

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private OrderStatus status;

    private BigDecimal orderPrice;
    private String paymentMethod;

    private LocalDate orderedAt;
    private LocalDate updatedAt;

    @Override
    public String toString() {
        return "🧾 Order Summary\n" +
                "-----------------------------\n" +
                "Order ID              : " + orderId + "\n" +
                "Customer Name         : " + customerName + "\n" +
                "Customer Number       : " + customerNumber + "\n" +
                "Service Provider      : " + serviceProviderName + "\n" +
                "Company Contact No.   : " + serviceProviderCompanyNumber + "\n" +
                "Service Name          : " + serviceName + "\n" +
                "Scheduled Date        : " + scheduledDate + "\n" +
                "Scheduled Time        : " + scheduledTime + "\n" +
                "Order Status          : " + status + "\n" +
                "Order Price           : ₹" + orderPrice + "\n" +
                "Payment Method        : " + paymentMethod + "\n" +
                "Service Address       : " + address + "\n" +
                "Ordered At            : " + orderedAt + "\n" +
                "Last Updated At       : " + updatedAt + "\n" +
                "-----------------------------\n" +
                "Thank you for choosing our service!";
    }


}
