package com.example.HomeService.service;

import com.example.HomeService.dto.ordersdto.OrderPaymentStatusUpdateDto;
import com.example.HomeService.dto.ordersdto.*;
import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.exceptions.UnauthorizedActionException;
import com.example.HomeService.model.*;
import com.example.HomeService.exceptions.OrderNotFoundException;
import com.example.HomeService.repository.*;
import com.example.HomeService.servicesinterface.OrdersServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrdersService implements OrdersServiceInterface {

    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ServicesRepository servicesRepository;
    private final PaymentRepository paymentRepository;
    private final JavaMailSender emailSender;
    private final OrderItemRepository orderItemRepository;


    public OrdersService(
            OrdersRepository ordersRepository,
            UsersRepository usersRepository,
            ServiceProviderRepository serviceProviderRepository,
            UserDetailsRepository userDetailsRepository,
            ServicesRepository servicesRepository,
            PaymentRepository paymentRepository,
            JavaMailSender emailSender,
            OrderItemRepository orderItemRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.servicesRepository = servicesRepository;
        this.paymentRepository = paymentRepository;
        this.emailSender = emailSender;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(OrderRegisterDto dto) {
        Orders order = new Orders();

        // Fetch user
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        // Fetch service provider
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider", dto.getServiceProviderId()));

        // Fetch user details
        UserDetails userDetails = userDetailsRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("UserDetails", dto.getUserId()));

        order.setCustomer(user);
        order.setServiceProvider(serviceProvider);
        order.setUserDetails(userDetails);
        order.setScheduledDate(dto.getScheduledDate());
        order.setScheduledTime(dto.getScheduledTime());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);

        // Default payment status to "UNPAID"
        order.setPaymentStatus("UNPAID");

        // Handle order items and calculate total price
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItemDto itemDto : dto.getItems()) {
            Services service = servicesRepository.findById(itemDto.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service", itemDto.getServiceId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setService(service);
            item.setQuantity(itemDto.getQuantity());
            item.setPricePerUnit(service.getPrice());

            orderItems.add(item);
            totalPrice = totalPrice.add(service.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        order.setItems(orderItems);
        order.setOrderPrice(totalPrice);

        // Save order before handling payment (important!)
        Orders savedOrder = ordersRepository.save(order);

        // Handle payment for ONLINE orders
        if ("ONLINE".equalsIgnoreCase(dto.getPaymentMethod())) {
            Payment payment = new Payment();
            payment.setOrder(savedOrder); // now the order has an ID
            payment.setAmount(totalPrice);
            payment.setPaymentStatus("PENDING"); // Default payment status until it's confirmed

            paymentRepository.save(payment);
            savedOrder.setPayment(payment); // optional, for bidirectional link
        }

        // Prepare response
        OrderResponseDto response = convertToResponseDto(savedOrder);
        OrderEmailDto emailDto = convertToEmailDto(savedOrder);

        // Send summary email (optional for now)
        sendSummaryEmail(savedOrder.getCustomer().getEmail(), emailDto);
        sendSummaryEmail(savedOrder.getServiceProvider().getUser().getEmail(), emailDto);

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
        order.setUpdatedAt(LocalDate.now());

        Orders updatedOrder = ordersRepository.save(order);

        OrderResponseDto response = convertToResponseDto(updatedOrder);
        OrderEmailDto emailDto = convertToEmailDto(order);

//         Sending to User
        sendSummaryEmail(order.getCustomer().getEmail(), emailDto);
//         Sending to Provider
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

    @Transactional
    public ResponseEntity<Map<String, String>> updatePaymentStatus(OrderPaymentStatusUpdateDto dto) {
        Map<String, String> response = new HashMap<>();

        // Fetch the order using the orderId from DTO
        Orders order = ordersRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", dto.getOrderId()));

        // Fetch service provider using the serviceProviderId from DTO
        ServiceProvider serviceProvider = serviceProviderRepository.findById(dto.getServiceProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProvider not found", dto.getServiceProviderId()));

        // Check if the service provider is authorized to update the payment status
        if (!order.getServiceProvider().getServiceProviderId().equals(serviceProvider.getServiceProviderId())) {
            response.put("failed", "Unauthorized action: Service provider not associated with this order.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Update the payment status in the order and save
        order.setPaymentStatus(dto.getPaymentStatus()); // Payment status updated based on DTO
        ordersRepository.save(order);

        // Return success response
        response.put("success", "Payment status updated successfully.");
        return ResponseEntity.ok(response);
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
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getService().getServiceId(),
                        item.getService().getServiceName(),
                        item.getQuantity()
                ))
                .toList();

        String paymentStatus = order.getPayment() != null ? order.getPayment().getPaymentStatus() : "N/A";

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
                order.getScheduledDate(),
                order.getScheduledTime(),
                order.getStatus(),
                order.getPaymentMethod(),
                paymentStatus,
                itemDtos,
                order.getOrderPrice(),
                order.getOrderedAt(),
                order.getUpdatedAt()
        );
    }


    private OrderEmailDto convertToEmailDto(Orders order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getService().getServiceId(),
                        item.getService().getServiceName(),
                        item.getQuantity()
                ))
                .toList();

        String paymentStatus = order.getPayment() != null ? order.getPayment().getPaymentStatus() : "PENDING";

        return new OrderEmailDto(
                order.getId(),
                order.getCustomer().getName(),
                order.getCustomer().getPhoneNumber(),
                order.getServiceProvider().getCompanyName(),
                order.getServiceProvider().getCompanyNumber(),
                order.getUserDetails().getAddress(),
                order.getScheduledDate(),
                order.getScheduledTime(),
                order.getStatus(),
                order.getPaymentMethod(),
                paymentStatus,
                itemDtos,
                order.getOrderPrice(),
                order.getOrderedAt(),
                order.getUpdatedAt()
        );
    }
}
