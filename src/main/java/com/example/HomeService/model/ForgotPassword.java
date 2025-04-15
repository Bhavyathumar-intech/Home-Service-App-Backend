package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private long OTP;

    private LocalDateTime createdAt;

    @Column(name = "Valid")
    private boolean validFlag;

    @Column(name = "verified")
    private boolean verified;

    private String resetToken;
}