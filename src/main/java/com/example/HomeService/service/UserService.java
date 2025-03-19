package com.example.HomeService.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.HomeService.dto.userDto.UserRegisterDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.HomeService.model.Users;
import com.example.HomeService.model.Role;
import com.example.HomeService.repo.UserRepository;
import com.example.HomeService.repo.ServiceProviderRepository;
import com.example.HomeService.model.ServiceProvider;

@Service
public class UserService {

    @Autowired
    private JWTservice jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users register(UserRegisterDto userDto) {
        Users user = new Users();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(encoder.encode(userDto.getPassword()));

        if (userDto.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            user.setRole(Role.valueOf(String.valueOf(userDto.getRole())));
        }

        repo.save(user);
        return user;
    }

    public ResponseEntity<?> verify(Users user, HttpServletResponse response) {
        Users dbUser = repo.findByEmail(user.getEmail());

        if (dbUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (user.getRole() != dbUser.getRole()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid role");
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                Long serviceProviderId = null;
                if (dbUser.getRole() == Role.PROVIDER) {
                    serviceProviderId = serviceProviderRepository.findByUserId(dbUser.getId())
                            .map(ServiceProvider::getServiceProviderId)
                            .orElse(null);
                }

                String jwtToken = jwtService.generateToken(
                        dbUser.getEmail(),
                        dbUser.getRole().toString(),
                        dbUser.getId(),
                        serviceProviderId
                );

                ResponseCookie cookie = ResponseCookie.from("authToken", jwtToken)
                        .httpOnly(false)
                        .secure(false)
                        .path("/")
                        .sameSite("Lax")
                        .build();

                response.addHeader("Set-Cookie", cookie.toString());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", "Login successful");
                responseBody.put("user", dbUser);
                responseBody.put("serviceProviderId", serviceProviderId);

                return ResponseEntity.ok(responseBody);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }

    public List<Users> getData() {
        return repo.findAll();
    }
}
