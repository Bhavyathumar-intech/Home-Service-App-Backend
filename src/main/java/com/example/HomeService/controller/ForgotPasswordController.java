package com.example.HomeService.controller;

import com.example.HomeService.dto.forgotpassword.ResetPasswordDTO;
import com.example.HomeService.service.ForgotPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/auth/forgot-password")
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/generate-otp")
    private ResponseEntity<?> generateOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println(email);
        return forgotPasswordService.sendOtp(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Long OTP = Long.valueOf(request.get("otp"));
        return forgotPasswordService.verifyOtp(email, OTP);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        return forgotPasswordService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getResetToken()
        );
    }
}
