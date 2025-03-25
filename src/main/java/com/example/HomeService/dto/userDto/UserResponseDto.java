package com.example.HomeService.dto.userDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String role;
    private Long serviceProviderId; // Can be null if not registered as a provider
    private String token;
}
