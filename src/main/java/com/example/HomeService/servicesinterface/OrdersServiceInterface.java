package com.example.HomeService.servicesinterface;
import com.example.HomeService.dto.ordersdto.*;
import com.example.HomeService.model.OrderStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OrdersServiceInterface {

    ResponseEntity<OrderResponseDto> createOrder(OrderRegisterDto dto);

    ResponseEntity<OrderResponseDto> updateOrder(OrderUpdateDto dto);

    ResponseEntity<?> deleteOrder(Long id);

    ResponseEntity<List<OrderResponseDto>> getAllOrders();

    ResponseEntity<OrderResponseDto> getOrderById(Long id);

    ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProviderId(Long serviceProviderId);

    ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(Long userId);

    ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(OrderStatus status);

    ResponseEntity<OrderResponseDto> updateOrderStatus(Long orderId, OrderStatus newStatus);

    ResponseEntity<Map<String, String>> updatePaymentStatus(OrderPaymentStatusUpdateDto dto);
}
