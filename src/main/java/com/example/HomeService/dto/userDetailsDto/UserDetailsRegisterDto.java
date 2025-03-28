package com.example.HomeService.dto.userDetailsDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.*;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRegisterDto {

    private Long userId;
    private String address;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dateOfBirth;

//    private String profilePictureUrl;
}