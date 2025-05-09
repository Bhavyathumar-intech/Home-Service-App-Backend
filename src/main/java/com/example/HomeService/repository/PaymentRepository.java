package com.example.HomeService.repository;

import com.example.HomeService.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    Payment findBySessionId(String sessionId);

}
