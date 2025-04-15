package com.example.HomeService.repository;

import com.example.HomeService.model.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {

    // Used to invalidate all previous OTPs before saving new one
    @Modifying
    @Query("UPDATE ForgotPassword fp SET fp.validFlag = false " +
            "WHERE fp.validFlag = true " +
            "AND fp.email = :email " +
            "AND fp.createdAt <= :validSince")
    void invalidatePreviousOtps(String email, LocalDateTime validSince);

    @Modifying
    @Query("UPDATE ForgotPassword fp SET fp.validFlag = false " +
            "WHERE fp.validFlag = true AND fp.email = :email")
    void invalidateAllActiveOtps(String email);

    // Used when user submits OTP
    ForgotPassword findTopByEmailAndOTPAndValidFlagTrueOrderByCreatedAtDesc(String email, long otp);

    // Used when user submits reset token after verifying OTP
    Optional<ForgotPassword> findTopByEmailAndResetTokenOrderByCreatedAtDesc(String email, String resetToken);
}
