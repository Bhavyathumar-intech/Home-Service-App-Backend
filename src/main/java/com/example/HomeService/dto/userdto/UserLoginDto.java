package com.example.HomeService.dto.userdto;

import com.example.HomeService.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    public String email;
    public String password;
    public Role role;
}
