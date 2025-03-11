package com.example.HomeService.controller;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserService;

/**
 * REST Controller for handling user-related operations.
 * Provides endpoints for user registration, authentication, and retrieval of all users.
 */
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private HttpServletResponse response;

    /**
     * Endpoint for user registration.
     *
     * @param user User object containing registration details.
     * @return Registered user object.
     */
    @PostMapping("/auth/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);
    }

    /**
     * Endpoint for user login authentication.
     *
     * @param user User object containing login credentials.
     * @return ResponseEntity with authentication result.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        ResponseEntity<?> responseEntity = service.verify(user, response);

        if (responseEntity.getBody() instanceof Map) {
            Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        }
        return responseEntity;
    }

    /**
     * Endpoint to retrieve all registered users.
     *
     * @return List of all Users.
     */
    @GetMapping("/auth/AllData")
    public List<Users> getData() {
        return service.getData();
    }
}
