//package com.example.HomeService.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "payments")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Payment {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "order_id", nullable = false)
//    private Orders order;
//
//    @Column(nullable = false)
//    private BigDecimal amount;
//
//    @Column(name = "payment_status", nullable = false)
//    private String paymentStatus;
//
//    @Column(name = "stripe_payment_intent_id")
//    private String stripePaymentIntentId;
//
//    @Column(name = "paid_at")
//    private LocalDateTime paidAt;
//}

package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "session_url", columnDefinition = "TEXT")
    private String sessionUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
