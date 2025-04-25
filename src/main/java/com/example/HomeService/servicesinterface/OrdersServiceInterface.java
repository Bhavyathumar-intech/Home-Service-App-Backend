package com.example.HomeService.servicesinterface;
import com.example.HomeService.dto.ordersdto.*;
import com.example.HomeService.exceptions.OrderNotFoundException;
import com.example.HomeService.exceptions.PaymentUpdateNotAllowedException;
import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.OrderStatus;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OrdersServiceInterface {

    ResponseEntity<OrderResponseDto> createOrder(OrderRegisterDto dto) throws StripeException, ResourceNotFoundException;

    ResponseEntity<OrderResponseDto> updateOrder(OrderUpdateDto dto) throws ResourceNotFoundException;

    ResponseEntity<?> deleteOrder(Long id);

    ResponseEntity<List<OrderResponseDto>> getAllOrders();

    ResponseEntity<OrderResponseDto> getOrderById(Long id) throws ResourceNotFoundException;

    ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProviderId(Long serviceProviderId) throws ResourceNotFoundException;

    ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(Long userId) throws ResourceNotFoundException;

    ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(OrderStatus status);

    ResponseEntity<OrderResponseDto> updateOrderStatus(Long orderId, OrderStatus newStatus) throws OrderNotFoundException;

    ResponseEntity<?> updatePaymentStatus(OrderPaymentStatusUpdateDto dto) throws ResourceNotFoundException, PaymentUpdateNotAllowedException;
}
