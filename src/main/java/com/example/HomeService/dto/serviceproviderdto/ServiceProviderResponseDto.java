package com.example.HomeService.dto.serviceproviderdto;

import com.example.HomeService.model.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceProviderResponseDto {
    private Long serviceProviderId;
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private String companyNumber;
    private String companyName;
    private int experienceYears;
    private String address;
    private String imageUrl;
    private String joiningDate;


    public ServiceProviderResponseDto(ServiceProvider serviceProvider) {
        this.serviceProviderId = serviceProvider.getServiceProviderId();
        this.userId = serviceProvider.getUser().getId();
        this.userName = serviceProvider.getUser().getName();
        this.email = serviceProvider.getUser().getEmail();
        this.role = serviceProvider.getUser().getRole().toString();
        this.companyNumber = serviceProvider.getCompanyNumber();
        this.companyName = serviceProvider.getCompanyName();
        this.experienceYears = serviceProvider.getExperienceYears();
        this.address = serviceProvider.getAddress();
        this.imageUrl = serviceProvider.getImageUrl();
        this.joiningDate = serviceProvider.getJoiningDate().toString();
    }
}
