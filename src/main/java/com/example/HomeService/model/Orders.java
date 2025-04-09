//package com.example.HomeService.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//
//@Entity
//@Table(name = "orders")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Orders {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users customer;
//
//    @ManyToOne
//    @JoinColumn(name = "service_provider_id", nullable = false)
//    private ServiceProvider serviceProvider;
//
//    /**
//     * Many-to-One relationship with UserDetails.
//     * Used for fetching the address and location details for the order.
//     */
//    @ManyToOne
//    @JoinColumn(name = "user_details_id", nullable = false)
//    private UserDetails userDetails;
//
//    @OneToOne
//    @Column(name = "service_name", nullable = false)
//    private Services services;
//
//    @Column(name = "scheduled_date_time", nullable = false)
//    private LocalDateTime scheduledDateTime;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "status", nullable = false)
//    private OrderStatus status;
//
//    private String paymentMethod;
//
//    /**
//     * Timestamp when the order was created.
//     */
//    @Column(name = "ordered_at", nullable = false, updatable = false)
//    private LocalDate orderedAt;
//
//    /**
//     * Timestamp when the order was last updated.
//     */
//    @Column(name = "updated_at")
//    private LocalDate updatedAt;
//
//    /**
//     * Automatically sets creation and update timestamps.
//     */
//    @PrePersist
//    protected void onCreate() {
//        this.orderedAt = LocalDate.now();
//        this.updatedAt = this.orderedAt;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = LocalDate.now();
//    }
//}


package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users customer;

    @ManyToOne
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    /**
     * Many-to-One relationship with UserDetails.
     * Used for fetching the address and location details for the order.
     */
    @ManyToOne
    @JoinColumn(name = "user_details_id", nullable = false)
    private UserDetails userDetails;

    /**
     * Many-to-One relationship with Services.
     * Used for referencing the services related to the order.
     */
    @ManyToOne
    @JoinColumn(name = "service_name", nullable = false)
    private Services services;

    @Column(name = "scheduled_date_time", nullable = false)
    private LocalDateTime scheduledDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    private String paymentMethod;

    /**
     * Timestamp when the order was created.
     */
    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDate orderedAt;

    /**
     * Timestamp when the order was last updated.
     */
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    /**
     * Automatically sets creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        this.orderedAt = LocalDate.now();
        this.updatedAt = this.orderedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
