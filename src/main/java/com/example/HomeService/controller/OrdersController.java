package com.example.HomeService.controller;

import com.example.HomeService.dto.ordersdto.OrderPaymentStatusUpdateDto;
import com.example.HomeService.dto.ordersdto.*;
import com.example.HomeService.model.OrderStatus;
import com.example.HomeService.service.OrdersService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRegisterDto dto) throws StripeException {
        return ordersService.createOrder(dto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PatchMapping("/update-order")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderUpdateDto dto) {
        return ordersService.updateOrder(dto);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ordersService.updateOrderStatus(orderId, status);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        return ordersService.deleteOrder(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-service-provider/{serviceProviderId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProvider(@PathVariable Long serviceProviderId) {
        return ordersService.getOrdersByServiceProviderId(serviceProviderId);
    }

    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable Long userId) {
        return ordersService.getOrdersByUserId(userId);
    }

//    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
//    @GetMapping("/by-user-object/{userId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserObject(@PathVariable Long userId) {
//        return ordersService.getOrdersByUserObject(userId);
//    }
//
//    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
//    @GetMapping("/by-provider-object/{providerId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersByProviderObject(@PathVariable Long providerId) {
//        return ordersService.getOrdersByServiceProviderObject(providerId);
//    }

    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ordersService.getOrdersByStatus(status);
    }

    // Put method to update payment status
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/payment-status")
    public ResponseEntity<?> updatePaymentStatusToPaid(@RequestBody OrderPaymentStatusUpdateDto dto) {
        return ordersService.updatePaymentStatus(dto);
    }
}
