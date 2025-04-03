package com.example.HomeService.dto.userDetailsDto;

import com.example.HomeService.model.UserDetails;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
public class UserDetailsResponseDTO {
    private Long udId;
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String dateOfBirth;
    private String profilePictureUrl;

    // Constructor
    public UserDetailsResponseDTO(UserDetails userDetails) {
        this.udId = userDetails.getUdId();
        this.userId = userDetails.getUser().getId();
        this.name = userDetails.getUser().getName();
        this.email = userDetails.getUser().getEmail();
        this.phoneNumber = userDetails.getUser().getPhoneNumber();
        this.address = userDetails.getAddress();
        this.city = userDetails.getCity();
        this.state = userDetails.getState();
        this.country = userDetails.getCountry();
        this.zipCode = userDetails.getZipCode();
        this.dateOfBirth = formatDate(userDetails.getDateOfBirth()); // Fixed this
        this.profilePictureUrl = userDetails.getProfilePictureUrl();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        // Convert java.util.Date to LocalDate
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}