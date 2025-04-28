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

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * Creates a new order for the authenticated user.
     *
     * @param dto Order registration details
     * @return Created order response
     * @throws StripeException if payment processing fails
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRegisterDto dto) throws StripeException {
        return ordersService.createOrder(dto);
    }

    /**
     * Updates an existing order for the authenticated user.
     *
     * @param dto Order update details
     * @return Updated order response
     */
    @PreAuthorize("hasAnyRole('USER')")
    @PatchMapping("/update-order")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderUpdateDto dto) {
        return ordersService.updateOrder(dto);
    }

    /**
     * Updates the status of an order by service provider.
     *
     * @param orderId ID of the order to update
     * @param status New order status
     * @return Response entity with update result
     */
    @PreAuthorize("hasRole('PROVIDER')")
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ordersService.updateOrderStatus(orderId, status);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id ID of the order to delete
     * @return Response entity indicating the result
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        return ordersService.deleteOrder(id);
    }

    /**
     * Retrieves all orders (for authenticated users and providers).
     *
     * @return List of all orders
     */
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ordersService.getAllOrders();
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id ID of the order
     * @return Order details
     */
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ordersService.getOrderById(id);
    }

    /**
     * Retrieves all orders associated with a specific service provider.
     *
     * @param serviceProviderId ID of the service provider
     * @return List of orders for the service provider
     */
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-service-provider/{serviceProviderId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByServiceProvider(@PathVariable Long serviceProviderId) {
        return ordersService.getOrdersByServiceProviderId(serviceProviderId);
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId ID of the user
     * @return List of orders for the user
     */
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable Long userId) {
        return ordersService.getOrdersByUserId(userId);
    }

//    /**
//     * Retrieves all orders placed by a specific user as full user object.
//     *
//     * @param userId ID of the user
//     * @return List of orders for the user
//     */
//    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
//    @GetMapping("/by-user-object/{userId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserObject(@PathVariable Long userId) {
//        return ordersService.getOrdersByUserObject(userId);
//    }
//
//    /**
//     * Retrieves all orders handled by a specific service provider object.
//     *
//     * @param providerId ID of the service provider
//     * @return List of orders
//     */
//    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
//    @GetMapping("/by-provider-object/{providerId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersByProviderObject(@PathVariable Long providerId) {
//        return ordersService.getOrdersByServiceProviderObject(providerId);
//    }

    /**
     * Retrieves orders filtered by their status.
     *
     * @param status Status of the orders to retrieve
     * @return List of orders with specified status
     */
    @PreAuthorize("hasAnyRole('USER', 'PROVIDER')")
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ordersService.getOrdersByStatus(status);
    }

    /**
     * Updates the payment status of an order to "Paid" by the service provider.
     *
     * @param dto Payment status update details
     * @return Response entity indicating the result
     */
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/payment-status")
    public ResponseEntity<?> updatePaymentStatusToPaid(@RequestBody OrderPaymentStatusUpdateDto dto) {
        return ordersService.updatePaymentStatus(dto);
    }
}
