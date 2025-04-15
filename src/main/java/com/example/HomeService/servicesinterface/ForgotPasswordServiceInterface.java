package com.example.HomeService.servicesinterface;


import org.springframework.http.ResponseEntity;

public interface ForgotPasswordServiceInterface {

    ResponseEntity<?> sendOtp(String email);

    ResponseEntity<?> verifyOtp(String email, long otp);

    ResponseEntity<?> resetPassword(String email, String newPassword, String resetToken);
}
