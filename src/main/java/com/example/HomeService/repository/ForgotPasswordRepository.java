package com.example.HomeService.repository;

import com.example.HomeService.model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    Optional<ForgotPassword> findByEmail(String email);

    Optional<ForgotPassword> findByEmailAndOTP(String email, long OTP);

    boolean existsByEmail(String email);
}