package com.example.HomeService.controller;

import com.example.HomeService.dto.OrdersDto.*;
import com.example.HomeService.model.OrderStatus;
import com.example.HomeService.service.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRegisterDto dto) {
        return ordersService.createOrder(dto);
    }

    @PutMapping("/update-order")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderUpdateDto dto) {
        return ordersService.updateOrder(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        return ordersService.deleteOrder(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

    @GetMapping("/by-service-provider/{serviceProviderId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProvider(@PathVariable Long serviceProviderId) {
        return ordersService.getOrdersByServiceProviderId(serviceProviderId);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable Long userId) {
        return ordersService.getOrdersByUserId(userId);
    }

    @GetMapping("/by-user-object/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserObject(@PathVariable Long userId) {
        return ordersService.getOrdersByUserObject(userId);
    }

    @GetMapping("/by-provider-object/{providerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByProviderObject(@PathVariable Long providerId) {
        return ordersService.getOrdersByServiceProviderObject(providerId);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ordersService.getOrdersByStatus(status);
    }

    @GetMapping("/by-schedule-range")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByScheduleRange(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end) {
        return ordersService.getOrdersByScheduleRange(start, end);
    }

}
