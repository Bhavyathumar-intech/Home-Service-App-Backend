package com.example.HomeService.service;

import com.example.HomeService.exceptions.ResourceNotFoundException;
import com.example.HomeService.model.ForgotPassword;
import com.example.HomeService.model.Users;
import com.example.HomeService.repository.ForgotPasswordRepository;
import com.example.HomeService.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private final ForgotPasswordRepository forgotPasswordRepository;
    private final UserRepository userRepository;
    private final JavaMailSender emailSender;


    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_MINUTES = 5;

    public ForgotPasswordService(ForgotPasswordRepository forgotPasswordRepository, UserRepository userRepository, JavaMailSender emailSender) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    // Method to generate OTP
    private long generateOTP() {
        Random random = new Random();
        long otp = 100000 + random.nextInt(900000); // Generates a 6-digit OTP
        return otp;
    }

    // Check if email exists in the user table and generate OTP
    @Transactional
    public ForgotPassword generateOTPForEmail(String email) {
        // Check if user exists with the given email
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: ", email);
        }
        // Check if an OTP already exists for the user and is valid (within 5 minutes)
        ForgotPassword existingRecord = forgotPasswordRepository.findByEmail(email).orElse(null);

        if (existingRecord != null && existingRecord.isFlag() && !existingRecord.isVerified() &&
                LocalDateTime.now().minusMinutes(OTP_VALIDITY_MINUTES).isBefore(existingRecord.getCreatedAt())) {
            // OTP is still valid for the email, no need to generate a new one
            return existingRecord;
        }

        // Create a new OTP record if previous one is invalid or expired
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail(email);
        forgotPassword.setOTP(generateOTP());
        forgotPassword.setVerified(false);
        forgotPassword.setFlag(true);
        forgotPassword.setCreatedAt(LocalDateTime.now());  // Setting the creation time of the OTP

        return forgotPasswordRepository.save(forgotPassword);
    }

    // Verify the OTP and update the verification flag and validity
    @Transactional
    public boolean verifyOTP(String email, long otp) {
        ForgotPassword forgotPassword = forgotPasswordRepository.findByEmailAndOTP(email, otp)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP or OTP not found"));

        if (!forgotPassword.isFlag()) {
            throw new RuntimeException("OTP has expired or is invalid");
        }

        // Mark OTP as verified
        forgotPassword.setVerified(true);
        forgotPassword.setFlag(false); // OTP is no longer valid after verification
        forgotPasswordRepository.save(forgotPassword);

        return true;
    }

    // Regenerate OTP for an existing email
    @Transactional
    public ForgotPassword regenerateOTP(String email) {
        ForgotPassword existingRecord = forgotPasswordRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No OTP record found for the given email"));

        if (existingRecord.isVerified()) {
            throw new RuntimeException("OTP already verified for this email.");
        }

        // If the OTP is expired or invalid, generate a new one
        return generateOTPForEmail(email);
    }

    @Async
    private void sendOTPEmail(String email, Long OTP) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP for Resetting Password ");
        message.setText(OTP.toString());
        emailSender.send(message);
    }
}
