package com.example.HomeService.service;

import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.ForgotPassword;
import com.example.HomeService.model.Users;
import com.example.HomeService.repository.ForgotPasswordRepository;
import com.example.HomeService.repository.UsersRepository;
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
public class ForgotPasswordService {

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

    //    @Transactional
//    public ResponseEntity<?> sendOtp(String email) {
//        Users user = usersRepository.findByEmail(email);
//        if (user == null) {
//            throw new RuntimeException("Email not found. Please register first.");
//        }
//
//        // Invalidate old OTPs older than 5 mins
//        forgotPasswordRepository.invalidatePreviousOtps(email, LocalDateTime.now().minusMinutes(OTP_VALIDITY_MINUTES));
//
//        long otp = generateSixDigitOtp();
//
//        ForgotPassword forgotPassword = new ForgotPassword();
//        forgotPassword.setEmail(email);
//        forgotPassword.setOTP(otp);
//        forgotPassword.setCreatedAt(LocalDateTime.now());
//        forgotPassword.setValidFlag(true);
//        forgotPassword.setVerified(false);
//        forgotPassword.setResetToken(null); // clear any old token
//
//        forgotPasswordRepository.save(forgotPassword);
//
//        sendOTPEmail(email, otp);
//        System.out.println("OTP for " + email + ": " + otp);
//
//        return ResponseEntity.ok("OTP sent successfully.");
//    }
    @Transactional
    public ResponseEntity<?> sendOtp(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email not found. Please register first.");
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

        return ResponseEntity.ok("OTP sent successfully.");
    }


    @Transactional
    public ResponseEntity<?> verifyOtp(String email, long otp) {
        ForgotPassword record = forgotPasswordRepository
                .findTopByEmailAndOTPAndValidFlagTrueOrderByCreatedAtDesc(email, otp);

        if (record == null) {
            throw new ResourceNotFoundException("Invalid OTP or already used.");
        }

        LocalDateTime expiryTime = record.getCreatedAt().plusMinutes(OTP_VALIDITY_MINUTES);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            record.setValidFlag(false);
            forgotPasswordRepository.save(record);
            throw new ResourceNotFoundException("OTP expired. Please request a new one.");
        }

        record.setVerified(true);
        record.setValidFlag(false);

        String resetToken = UUID.randomUUID().toString();
        record.setResetToken(resetToken); // generate and store reset token

        forgotPasswordRepository.save(record);

        // Return resetToken in JSON format
        Map<String, String> response = new HashMap<>();
        response.put("resetToken", resetToken);

        return ResponseEntity.ok(response);
    }


    @Transactional
    public ResponseEntity<?> resetPassword(String email, String newPassword, String resetToken) {
        Optional<ForgotPassword> optional = forgotPasswordRepository
                .findTopByEmailAndResetTokenOrderByCreatedAtDesc(email, resetToken);

        if (optional.isEmpty()) {
            throw new RuntimeException("Invalid reset token.");
        }

        ForgotPassword record = optional.get();

        if (!record.isVerified()) {
            throw new RuntimeException("Reset token not verified via OTP.");
        }

        LocalDateTime expiryTime = record.getCreatedAt().plusMinutes(OTP_VALIDITY_MINUTES);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            throw new RuntimeException("Reset token expired. Please restart process.");
        }

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        // Invalidate token
        record.setResetToken(null);
        forgotPasswordRepository.save(record);

        sendConfirmationEmail(email);

        return ResponseEntity.ok("Password reset successfully.");
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
