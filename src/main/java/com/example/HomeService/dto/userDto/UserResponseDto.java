package com.example.HomeService.dto.userDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private Long serviceProviderId; // Can be null if not registered as a provider
    private String token;

//    public UserResponseDto(Long userId, String email, String role, Long serviceProviderId, String token, String name) {
//        this.userId = userId;
//        this.email = email;
//        this.role = role;
//        this.serviceProviderId = serviceProviderId;
//        this.token = token;
//        this.name = name;
//    }
}
