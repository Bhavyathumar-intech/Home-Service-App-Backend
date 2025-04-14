package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @Temporal(TemporalType.DATE)
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal orderPrice;

    @Column(nullable = false)
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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
