package com.example.HomeService.dto.serviceProviderDto;

import com.example.HomeService.model.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ServiceProviderUpdateDto {

        private Long serviceProviderId;
        private String companyNumber;
        private String companyName;
        private int experienceYears;
        private String address;
        private String imageUrl;


        public ServiceProviderUpdateDto(ServiceProvider serviceProvider) {
            this.serviceProviderId = serviceProvider.getServiceProviderId();
            this.companyNumber = serviceProvider.getCompanyNumber();
            this.companyName = serviceProvider.getCompanyName();
            this.experienceYears = serviceProvider.getExperienceYears();
            this.address = serviceProvider.getAddress();
            this.imageUrl = serviceProvider.getImageUrl();
        }
    }
