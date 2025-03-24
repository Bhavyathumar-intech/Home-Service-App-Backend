package com.example.HomeService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing user details.
 * Stores additional information about the user, linked with the Users entity.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_details")
public class UserDetails {

    /**
     * Primary key for the user details table.
     * Auto-generated unique identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ud_id")
    private Long udId;

    /**
     * One-to-one relationship with Users entity.
     * Each user has exactly one set of user details.
     */
    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false, unique = true)
    private Users user;

    /**
     * Address details of the user.
     */
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    /**
     * Date of birth of the user.
     * Stored in the format "dd-MM-yyyy".
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * URL to the user's profile picture.
     */
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
}
