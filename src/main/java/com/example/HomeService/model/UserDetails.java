//package com.example.HomeService.model;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "user_details")
//public class UserDetails {
//
//    @Id
//    @OneToOne
//    @MapsId  // Uses 'id' from Users as PK & FK
//    @JoinColumn(name = "user_id")  // Ensures column name is 'user_id'
//    private Users user;
//
//    @Column(name = "address", nullable = false)
//    private String address;
//
//    @Column(name = "city", nullable = false)
//    private String city;
//
//    @Column(name = "state", nullable = false)
//    private String state;
//
//    @Column(name = "country", nullable = false)
//    private String country;
//
//    @Column(name = "zip_code", nullable = false)
//    private String zipCode;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @Column(name = "date_of_birth", nullable = false)
//    private Date dateOfBirth;
//
//    @Column(name = "profile_picture_url")
//    private String profilePictureUrl;
//}

package com.example.HomeService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_details")
public class UserDetails {

    @Id  // ✅ Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ Auto-increment PK
    @Column(name = "ud_id")
    private Long udId;

    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false, unique = true) // ✅ Auto-increment FK
    private Users user;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
}
