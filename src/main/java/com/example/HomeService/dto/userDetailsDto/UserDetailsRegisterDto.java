package com.example.HomeService.dto.userDetailsDto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    private String profilePictureUrl;
}