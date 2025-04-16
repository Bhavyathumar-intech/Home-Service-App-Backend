package com.example.HomeService.service;

import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.ForgotPassword;
import com.example.HomeService.model.Users;
import com.example.HomeService.repository.ForgotPasswordRepository;
import com.example.HomeService.repository.UsersRepository;
import com.example.HomeService.servicesinterface.ForgotPasswordServiceInterface;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ForgotPasswordService implements ForgotPasswordServiceInterface {

    private static final int OTP_VALIDITY_MINUTES = 5;

    private final UsersRepository usersRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final JavaMailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordService(UsersRepository usersRepository,
                                 ForgotPasswordRepository forgotPasswordRepository,
                                 JavaMailSender emailSender,
                                 PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public ResponseEntity<?> sendOtp(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Email not found. Please register first.");
        }

        // Invalidate ALL previous active OTPs for this email (even recent ones)
        forgotPasswordRepository.invalidateAllActiveOtps(email);

        long otp = generateSixDigitOtp();

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail(email);
        forgotPassword.setOTP(otp);
        forgotPassword.setCreatedAt(LocalDateTime.now());
        forgotPassword.setValidFlag(true);
        forgotPassword.setVerified(false);
        forgotPassword.setResetToken(null); // clear any old token

        forgotPasswordRepository.save(forgotPassword);

        sendOTPEmail(email, otp);
        System.out.println("OTP for " + email + ": " + otp);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "OTP sent successfully.");
        response.put("email", email);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<?> verifyOtp(String email, long otp) {
        // Find the most recent valid OTP entry for the email
        ForgotPassword record = forgotPasswordRepository
                .findTopByEmailAndOTPAndValidFlagTrueOrderByCreatedAtDesc(email, otp);

        // If no valid OTP record is found, throw an error
        if (record == null) {
            throw new ResourceNotFoundException("Invalid OTP or already used.");
        }

        // Check if OTP is expired
        LocalDateTime expiryTime = record.getCreatedAt().plusMinutes(OTP_VALIDITY_MINUTES);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            // Invalidate the OTP if expired
            record.setValidFlag(false);
            forgotPasswordRepository.save(record);
            throw new ResourceNotFoundException("OTP expired. Please request a new one.");
        }

        // Mark the OTP as verified and invalidate it
        record.setVerified(true);
        record.setValidFlag(false);

        // Generate a reset token and store it
        String resetToken = UUID.randomUUID().toString();
        record.setResetToken(resetToken);

        // Save changes to the database
        forgotPasswordRepository.save(record);

        // Create JSON response with message and resetToken
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP verified successfully.");
        response.put("resetToken", resetToken);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<?> resetPassword(String email, String newPassword, String resetToken) {
        // Find the most recent ForgotPassword record for the email and reset token
        Optional<ForgotPassword> optional = forgotPasswordRepository
                .findTopByEmailAndResetTokenOrderByCreatedAtDesc(email, resetToken);

        // Validate the reset token
        if (optional.isEmpty()) {
            throw new RuntimeException("Invalid or missing reset token.");
        }

        ForgotPassword record = optional.get();

        // Ensure the token has been verified via OTP
        if (!record.isVerified()) {
            throw new RuntimeException("Reset token has not been verified. Please verify OTP first.");
        }

        // Check if token is still valid (not expired)
        LocalDateTime expiryTime = record.getCreatedAt().plusMinutes(OTP_VALIDITY_MINUTES);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            throw new RuntimeException("Reset token has expired. Please restart the process.");
        }

        // Find user by email
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User associated with this email was not found.");
        }

        // Update user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        // Invalidate the reset token so it can't be reused
        record.setResetToken(null);
        forgotPasswordRepository.save(record);

        // Optionally send a confirmation email
        sendConfirmationEmail(email);

        // Return a JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset successfully.");

        return ResponseEntity.ok(response);
    }


    @Async
    private void sendOTPEmail(String email, long OTP) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for password reset");
        message.setText("Your OTP is: " + OTP + ". It's valid for 5 minutes.");
        emailSender.send(message);
    }

    @Async
    private void sendConfirmationEmail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Successfully Changed the Password For Home-Service-Booking-App ");
        message.setText("For Email id" + email + " Password is Successfully changed ");
        emailSender.send(message);
    }

    private long generateSixDigitOtp() {
        return 100000 + new Random().nextInt(900000);
    }
}
