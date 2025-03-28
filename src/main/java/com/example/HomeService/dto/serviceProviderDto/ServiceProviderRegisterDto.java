package com.example.HomeService.dto.serviceProviderDto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderRegisterDto {
    private long userId;
    private String companyName;
    private int experienceYears;
    private String address;
    private String companyNumber;
}