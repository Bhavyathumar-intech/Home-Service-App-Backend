package com.example.HomeService.repository;

import com.example.HomeService.model.Orders;
import com.example.HomeService.model.Users;
import com.example.HomeService.model.ServiceProvider;
import com.example.HomeService.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // Find all orders by a specific user
    List<Orders> findByCustomer(Users user);

    // Find all orders for a specific service provider
    List<Orders> findByServiceProvider(ServiceProvider serviceProvider);

    // Find all orders with a specific status
    List<Orders> findByStatus(OrderStatus status);

    // Find orders by user ID (useful for performance if only ID is available)
    List<Orders> findByCustomerId(Long userId);

    // Find orders scheduled between two date-times
//    List<Orders> findByScheduledDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Orders> findByServiceProvider_ServiceProviderId(Long serviceProviderId);


}