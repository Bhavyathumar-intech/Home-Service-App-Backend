package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false, unique = true)
    private Long serviceId;
    /**
     * Many-to-One relationship with ServiceProvider.
     * One provider can offer multiple services.
     */
    @ManyToOne
    @JoinColumn(name = "serviceProvider_Id", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "expected_duration")
    private Integer expectedDuration;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Temporal(TemporalType.DATE)
    @Column(name = "updated_at", nullable = true)
    private LocalDate updatedAt;

    @Column(name = "status", nullable = false)
    private boolean status = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }

    @Column(nullable = true)
    private String image_url;
}
