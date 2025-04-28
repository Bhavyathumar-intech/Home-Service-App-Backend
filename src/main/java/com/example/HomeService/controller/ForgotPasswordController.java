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

    /**
     * Generates and sends an OTP (One Time Password) to the user's email.
     *
     * @param request A map containing the user's email address
     * @return ResponseEntity indicating success or failure of OTP generation
     */
    @PostMapping("/generate-otp")
    private ResponseEntity<?> generateOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        System.out.println(email);
        return forgotPasswordService.sendOtp(email);
    }

    /**
     * Verifies the OTP provided by the user.
     *
     * @param request A map containing the user's email and OTP
     * @return ResponseEntity indicating whether OTP verification was successful
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Long OTP = Long.valueOf(request.get("otp"));
        return forgotPasswordService.verifyOtp(email, OTP);
    }

    /**
     * Resets the user's password after successful OTP verification.
     *
     * @param request A ResetPasswordDTO containing email, new password, and reset token
     * @return ResponseEntity indicating success or failure of password reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        return forgotPasswordService.resetPassword(
                request.getEmail(),
                request.getNewPassword(),
                request.getResetToken()
        );
    }
}
