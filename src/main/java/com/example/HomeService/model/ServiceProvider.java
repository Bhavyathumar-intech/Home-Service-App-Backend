//package com.example.HomeService.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//
//@Entity
//@Table(name = "service_providers")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ServiceProvider {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
//    @Column(name = "serviceProvider_Id", nullable = false, unique = true)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_Id", nullable = false, unique = true)
//    private Users user;  // Link to Users table
//
//    @Column(name = "company_name", nullable = false)
//    private String companyName;
//
//    @Column(name = "experience_years")
//    private int experienceYears;
//
//    @Column(name = "address", nullable = false)
//    private String address;
//
//    @Column(name = "phone_number", nullable = false, unique = true)
//    private String phoneNumber;
//
//    @Column(name = "joining_date", nullable = false, updatable = false)
//    private LocalDate joiningDate; // Automatically set when the provider is created
//
//    @Column(name = "image_url", nullable = true)  // ✅ New field (can be null)
//    private String imageUrl;
//
//    // Constructor without ID (as it is auto-generated)
//    public ServiceProvider(Users user, String companyName, int experienceYears, String address, String imageUrl) {
//        this.user = user;
//        this.companyName = companyName;
//        this.experienceYears = experienceYears;
//        this.address = address;
//        this.phoneNumber = user.getPhoneNumber(); // Fetch phone number from Users entity
//        this.joiningDate = LocalDate.now(); // Automatically set current date
//        this.imageUrl = imageUrl;  // ✅ Optional image URL
//    }
//
//    @PrePersist
//    protected void onCreate() {
//        this.joiningDate = LocalDate.now(); // Ensure joiningDate is set before persisting
//    }
//
//    @Override
//    public String toString() {
//        return "ServiceProvider{" +
//                "id=" + id +
//                ", name=" + user.getName() +  // Fetch provider's name from Users table
//                ", companyName='" + companyName + '\'' +
//                ", experienceYears=" + experienceYears +
//                ", address='" + address + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                ", joiningDate=" + joiningDate +
//                ", imageUrl='" + imageUrl + '\'' +
//                '}';
//    }
//}

package com.example.HomeService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    @Column(name = "serviceProvider_Id", nullable = false, unique = true)
    private Long serviceProviderId;  // ✅ Changed from "id" to "serviceProviderId"

    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false, unique = true)
    private Users user;  // Link to Users table

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "experience_years")
    private int experienceYears;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "joining_date", nullable = false, updatable = false)
    private LocalDate joiningDate; // Automatically set when the provider is created

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    // Constructor without ID (as it is auto-generated)
    public ServiceProvider(Users user, String companyName, int experienceYears, String address, String imageUrl) {
        this.user = user;
        this.companyName = companyName;
        this.experienceYears = experienceYears;
        this.address = address;
        this.phoneNumber = user.getPhoneNumber();
        this.joiningDate = LocalDate.now();
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.joiningDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "serviceProviderId=" + serviceProviderId +
                ", name=" + user.getName() +
                ", companyName='" + companyName + '\'' +
                ", experienceYears=" + experienceYears +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", joiningDate=" + joiningDate +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
