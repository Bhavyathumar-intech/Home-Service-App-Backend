package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity class representing a Service Provider.
 */
@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

    /**
     * Unique identifier for the service provider.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceProvider_Id", nullable = false, unique = true)
    private Long serviceProviderId;

    /**
     * One-to-One relationship with the Users table.
     */
    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false, unique = true)
    private Users user;

    /**
     * Name of the company the service provider is associated with.
     */
    @Column(name = "company_name", nullable = false)
    private String companyName;

    /**
     * Number of years of experience the provider has.
     */
    @Column(name = "experience_years")
    private int experienceYears;

    /**
     * Address of the service provider.
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Contact Company Number of the service provider.
     */
    @Column(name = "company_number", nullable = false, unique = true)
    private String companyNumber;

    /**
     * Date the provider joined the platform.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "joining_date", nullable = false, updatable = false)
    private LocalDate joiningDate;

    /**
     * Optional URL to an image of the service provider.
     */
    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    /**
     * Constructor without ID (as it is auto-generated).
     *
     * @param user            The linked User entity.
     * @param companyName     Name of the company.
     * @param experienceYears Years of experience.
     * @param address         Address of the service provider.
     */
    public ServiceProvider(Users user, String companyName, int experienceYears, String address, String companyNumber) {
        this.user = user;
        this.companyName = companyName;
        this.experienceYears = experienceYears;
        this.address = address;
        this.companyNumber = companyNumber;
        this.joiningDate = LocalDate.now();
    }

    /**
     * Sets the joining date automatically before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        this.joiningDate = LocalDate.now();
    }

    /**
     * Returns a string representation of the ServiceProvider entity.
     *
     * @return A formatted string with provider details.
     */
    @Override
    public String toString() {
        return "ServiceProvider{" +
                "serviceProviderId=" + serviceProviderId +
                ", name=" + user.getName() +
                ", companyName='" + companyName + '\'' +
                ", experienceYears=" + experienceYears +
                ", address='" + address + '\'' +
                ", phoneNumber='" + companyNumber + '\'' +
                ", joiningDate=" + joiningDate +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
