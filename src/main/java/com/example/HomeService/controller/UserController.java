package com.example.HomeService.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.HomeService.dto.userdto.UserRegisterDto;
import com.example.HomeService.dto.userdto.UserLoginDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserService;

@RestController
@CrossOrigin
public class UserController {

    private final UserService service;
    private final HttpServletResponse response;

    public UserController(UserService service, HttpServletResponse response) {
        this.service = service;
        this.response = response;
    }

    /**
     * Registers a new user.
     *
     * @param userDto Data Transfer Object containing user registration details.
     * @return The created user entity.
     */
    @PostMapping("/auth/register")
    public Users register(@RequestBody UserRegisterDto userDto) {
        return service.register(userDto);
    }

    /**
     * Authenticates a user by verifying the login credentials.
     *
     * @param userLoginDto Data Transfer Object containing login credentials (email, password, role).
     * @return ResponseEntity with either success data or error message.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        // Convert DTO to Entity

        Users user = new Users();
        user.setEmail(userLoginDto.getEmail());
        user.setPassword(userLoginDto.getPassword());
        user.setRole(userLoginDto.getRole());

        // Call the service layer for verification
        ResponseEntity<?> responseEntity = service.verify(user, response);

        if (responseEntity.getBody() instanceof Map) {
            Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
            return ResponseEntity.ok(responseBody);
        }
        return responseEntity;
    }

    /**
     * Retrieves a list of all users in the system.
     *
     * @return A list of all users.
     */
    @GetMapping("/api/AllData")
    public List<Users> getData() {
        return service.getData();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to be deleted.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @DeleteMapping("/auth/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            // Call the service method to delete the user
            return ResponseEntity.ok(service.deleteUser(id).getBody());
        } catch (RuntimeException e) {
            response.put("Method fail", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
