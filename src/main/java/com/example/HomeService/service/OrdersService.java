package com.example.HomeService.service;

import com.example.HomeService.dto.ordersdto.*;
import com.example.HomeService.exceptions.OrderNotFoundException;
import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.*;
import com.example.HomeService.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ServicesRepository servicesRepository;
    private final JavaMailSender emailSender;

    public OrdersService(OrdersRepository ordersRepository, UsersRepository usersRepository, ServiceProviderRepository serviceProviderRepository, UserDetailsRepository userDetailsRepository, ServicesRepository servicesRepository, JavaMailSender emailSender) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.servicesRepository = servicesRepository;
        this.emailSender = emailSender;
    }

    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(OrderRegisterDto dto) {
        Orders order = new Orders();

        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Services servicesProviderID = servicesRepository.findById(dto.getServicesId()).get();
        ServiceProvider serviceProvider = serviceProviderRepository.findById(servicesProviderID.getServiceProvider().getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", servicesProviderID.getServiceProvider().getServiceProviderId()));

        UserDetails userDetailsid = userDetailsRepository.findByUserId(dto.getUserId()).get();

        UserDetails userDetails = userDetailsRepository.findById(userDetailsid.getUdId())
                .orElseThrow(() -> new ResourceNotFoundException("UserDetails", userDetailsid.getUdId()));

        Services services = servicesRepository.findById(dto.getServicesId())
                .orElseThrow(() -> new ResourceNotFoundException("Services", dto.getServicesId()));

        order.setCustomer(user);
        order.setServiceProvider(serviceProvider);
        order.setUserDetails(userDetails);
        order.setServices(services);
        order.setScheduledDate(dto.getScheduledDate());
        order.setScheduledTime(dto.getScheduledTime());
        order.setOrderPrice(dto.getOrderPrice());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);

        Orders savedOrder = ordersRepository.save(order);
        OrderResponseDto response = convertToResponseDto(savedOrder);
        OrderEmailDto emailDto = convertToEmailDto(savedOrder);

        // Sending to User
        sendSummaryEmail(order.getCustomer().getEmail(), emailDto);
        // Sending to Provider
        sendSummaryEmail(order.getServiceProvider().getUser().getEmail(), emailDto);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<OrderResponseDto> updateOrder(OrderUpdateDto dto) {
        Orders order = ordersRepository.findById(dto.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Order ID Not Found", dto.getOrderId()));


        if (dto.getScheduledDate() != null) {
            order.setScheduledDate(dto.getScheduledDate());
        }
        if (dto.getScheduledTime() != null) {
            order.setScheduledTime(dto.getScheduledTime());
        }
        if (dto.getStatus() != null) order.setStatus(dto.getStatus());
        if (dto.getPaymentMethod() != null) order.setPaymentMethod(dto.getPaymentMethod());
        order.setUpdatedAt(LocalDate.now());

        order.setOrderedAt(LocalDate.now());
        Orders updatedOrder = ordersRepository.save(order);

        OrderResponseDto response = convertToResponseDto(updatedOrder);
        OrderEmailDto emailDto = convertToEmailDto(order);

        // Sending to User
        sendSummaryEmail(order.getCustomer().getEmail(), emailDto);
        // Sending to Provider
        sendSummaryEmail(order.getServiceProvider().getUser().getEmail(), emailDto);

        return ResponseEntity.ok(response);
    }

    @Transactional
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
        List<Orders> orders = ordersRepository.findByServiceProvider_ServiceProviderId(serviceProviderId);
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

    // For Updating Only OrderStauts
    @Transactional
    public ResponseEntity<OrderResponseDto> updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDate.now());
        ordersRepository.save(order);

        OrderEmailDto responseDto = convertToEmailDto(order);
        // Async Method for sending Email
        sendSummaryEmail(order.getCustomer().getEmail(), responseDto);

        OrderResponseDto ord = convertToResponseDto(order);

        return ResponseEntity.ok(ord);
    }


    @Async
    private void sendSummaryEmail(String email, OrderEmailDto responseDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Order Summary of OrderId " + responseDto.getOrderId());
        message.setText(responseDto.toString());
        emailSender.send(message);
    }

    private OrderResponseDto convertToResponseDto(Orders order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getCustomer().getPhoneNumber(),
                order.getServiceProvider().getServiceProviderId(),
                order.getServiceProvider().getCompanyName(),
                order.getServiceProvider().getCompanyNumber(),
                order.getUserDetails().getUdId(),
                order.getUserDetails().getAddress(),
                order.getServices().getServiceId(),
                order.getServices().getServiceName(),
                order.getScheduledDate(),
                order.getScheduledTime(),
                order.getStatus(),
                order.getOrderPrice(),
                order.getPaymentMethod(),
                order.getOrderedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderEmailDto convertToEmailDto(Orders order) {
        return new OrderEmailDto(
                order.getId(),
                order.getCustomer().getName(),
                order.getCustomer().getPhoneNumber(),
                order.getServiceProvider().getCompanyName(),
                order.getServiceProvider().getCompanyNumber(),
                order.getUserDetails().getAddress(),
                order.getServices().getServiceName(),
                order.getScheduledDate(),
                order.getScheduledTime(),
                order.getStatus(),
                order.getOrderPrice(),
                order.getPaymentMethod(),
                order.getOrderedAt(),
                order.getUpdatedAt()
        );
    }
}
