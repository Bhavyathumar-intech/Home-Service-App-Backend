package com.example.HomeService.service;

import com.example.HomeService.dto.OrdersDto.*;
import com.example.HomeService.model.*;
import com.example.HomeService.repo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository usersRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ServicesRepository servicesRepository;

    public OrdersService(OrdersRepository ordersRepository, UserRepository usersRepository, ServiceProviderRepository serviceProviderRepository, UserDetailsRepository userDetailsRepository, ServicesRepository servicesRepository) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.servicesRepository = servicesRepository;
    }

    public ResponseEntity<OrderResponseDto> createOrder(OrderRegisterDto dto) {
        Orders order = new Orders();

        Users user = usersRepository.findById(dto.getUserId()).orElseThrow();
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId()).orElseThrow();
        UserDetails userDetails = userDetailsRepository.findById(dto.getUserDetailsId()).orElseThrow();
        Services services = servicesRepository.findById(dto.getServicesId()).orElseThrow();

        order.setCustomer(user);
        order.setServiceProvider(serviceProvider);
        order.setUserDetails(userDetails);
        order.setServices(services);
        order.setScheduledDateTime(dto.getScheduledDateTime());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING); // default status

        Orders savedOrder = ordersRepository.save(order);

        return ResponseEntity.ok(convertToResponseDto(savedOrder));
    }

    public ResponseEntity<OrderResponseDto> updateOrder(OrderUpdateDto dto) {
        Orders order = ordersRepository.findById(dto.getOrderId()).orElseThrow();

        if (dto.getScheduledDateTime() != null) order.setScheduledDateTime(dto.getScheduledDateTime());
        if (dto.getStatus() != null) order.setStatus(dto.getStatus());
        if (dto.getPaymentMethod() != null) order.setPaymentMethod(dto.getPaymentMethod());

        Orders updatedOrder = ordersRepository.save(order);

        return ResponseEntity.ok(convertToResponseDto(updatedOrder));
    }

    public ResponseEntity<?> deleteOrder(Long id) {
        try {
            if (!ordersRepository.existsById(id)) {
                return ResponseEntity.status(404).body(Collections.singletonMap("failed", "Order not found"));
            }
            ordersRepository.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("success", "Order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("failed", "Error occurred while deleting"));
        }
    }

    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> list = ordersRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<OrderResponseDto> getOrderById(Long id) {
        Orders order = ordersRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(convertToResponseDto(order));
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProviderId(Long serviceProviderId) {
        List<Orders> orders = ordersRepository.findByServiceProviderId(serviceProviderId);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(Long userId) {
        List<Orders> orders = ordersRepository.findByCustomerId(userId);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserObject(Long userId) {
        Users user = usersRepository.findById(userId).orElseThrow();
        List<Orders> orders = ordersRepository.findByCustomer(user);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProviderObject(Long serviceProviderId) {
        ServiceProvider sp = serviceProviderRepository.findById(serviceProviderId).orElseThrow();
        List<Orders> orders = ordersRepository.findByServiceProvider(sp);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(OrderStatus status) {
        List<Orders> orders = ordersRepository.findByStatus(status);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<OrderResponseDto>> getOrdersByScheduleRange(LocalDateTime start, LocalDateTime end) {
        List<Orders> orders = ordersRepository.findByScheduledDateTimeBetween(start, end);
        List<OrderResponseDto> response = orders.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }



    // --- Helper to map entity to response DTO ---
    private OrderResponseDto convertToResponseDto(Orders order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getServiceProvider().getServiceProviderId(),
                order.getServiceProvider().getCompanyName(),
                order.getUserDetails().getUdId(),
                order.getUserDetails().getAddress(),
                order.getServices().getServiceId(),
                order.getServices().getServiceName(),
                order.getScheduledDateTime(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getOrderedAt(),
                order.getUpdatedAt()
        );
    }
}
