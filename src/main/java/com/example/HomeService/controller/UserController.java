package com.example.HomeService.controller;

import java.util.List;
import java.util.Map;

import com.example.HomeService.dto.userDto.UserRegisterDto;
import com.example.HomeService.dto.userDto.UserLoginDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserService;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("/auth/register")
    public Users register(@RequestBody UserRegisterDto userDto) {
        return service.register(userDto);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        // Convert DTO to Entity
        Users user = new Users();
        user.setEmail(userLoginDto.getEmail());
        user.setPassword(userLoginDto.getPassword());
        user.setRole(userLoginDto.getRole());

        System.out.println(user.getPassword());
        System.out.println(user.toString());
        // Call the service layer for verification
        ResponseEntity<?> responseEntity = service.verify(user, response);

        if (responseEntity.getBody() instanceof Map) {
            Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        }
        return responseEntity;
    }

    @GetMapping("/auth/AllData")
    public List<Users> getData() {
        return service.getData();
    }
}
