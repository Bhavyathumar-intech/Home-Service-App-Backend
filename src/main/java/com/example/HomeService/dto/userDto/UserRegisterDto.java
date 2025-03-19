package com.example.HomeService.dto.userDto;

import com.example.HomeService.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private Role role;
}