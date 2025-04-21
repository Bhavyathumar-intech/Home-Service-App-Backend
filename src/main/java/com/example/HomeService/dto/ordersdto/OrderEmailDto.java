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
public class OrderEmailDto {

    private Long orderId;

    private String customerName;
    private String customerNumber;

    private String serviceProviderName;
    private String serviceProviderCompanyNumber;

    private String address;

    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    private OrderStatus status;

    private String paymentMethod;
    private String paymentStatus;

    private List<OrderItemDto> items;

    private BigDecimal totalAmount;

    private LocalDate orderedAt;
    private LocalDate updatedAt;

    @Override
    public String toString() {
        StringBuilder itemList = new StringBuilder();
        for (OrderItemDto item : items) {
            itemList.append("• ").append(item.getServiceName())
                    .append(" (Qty: ").append(item.getQuantity())
                    .append(", ₹").append(totalAmount).append(" total)\n");
        }

        return "🧾 Order Summary\n" +
                "-----------------------------\n" +
                "Order ID              : " + orderId + "\n" +
                "Customer Name         : " + customerName + "\n" +
                "Customer Number       : " + customerNumber + "\n" +
                "Service Provider      : " + serviceProviderName + "\n" +
                "Company Contact No.   : " + serviceProviderCompanyNumber + "\n" +
                "Scheduled Date        : " + scheduledDate + "\n" +
                "Scheduled Time        : " + scheduledTime + "\n" +
                "Order Status          : " + status + "\n" +
                "Payment Method        : " + paymentMethod + "\n" +
                "Payment Status        : " + paymentStatus + "\n" +
                "Service Address       : " + address + "\n" +
                "-----------------------------\n" +
                "Services:\n" + itemList +
                "-----------------------------\n" +
                "Total Amount          : ₹" + totalAmount + "\n" +
                "Ordered At            : " + orderedAt + "\n" +
                "Last Updated At       : " + updatedAt + "\n" +
                "-----------------------------\n" +
                "Thank you for choosing our service!";
    }
}
