package com.example.HomeService.controller;

import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user-details")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

     @PostMapping
    public ResponseEntity<?> registerUserDetails(@RequestBody Map<String, Object> userDetailsMap) {
        UserDetails userDetails = new UserDetails();

        // Handling user_Id correctly
        if (userDetailsMap.get("user_Id") != null) {
            Users user = new Users();
            user.setId(Long.parseLong(userDetailsMap.get("user_Id").toString())); // Safe conversion
            System.out.println(user.toString());
            userDetails.setUser(user);
        }

        userDetails.setAddress((String) userDetailsMap.get("address"));
        userDetails.setCity((String) userDetailsMap.get("city"));
        userDetails.setState((String) userDetailsMap.get("state"));
        userDetails.setCountry((String) userDetailsMap.get("country"));
        userDetails.setZipCode((String) userDetailsMap.get("zipCode"));

        // Parse dateOfBirth safely
        userDetails.setDateOfBirth(LocalDate.parse((String) userDetailsMap.get("dateOfBirth")));

        userDetails.setProfilePictureUrl((String) userDetailsMap.get("profilePictureUrl"));

        return userDetailsService.saveOrUpdateUserDetails(userDetails.getUser().getId(), userDetails);
    }


    // ✅ PUT request for updating existing user details (User ID required in JSON)
    @PutMapping
    public ResponseEntity<?> updateUserDetails(@RequestBody Map<String, Object> request) {
        Long userId = request.get("user_Id") instanceof Number ?
                ((Number) request.get("user_Id")).longValue() : null;

        if (userId == null) {
            return ResponseEntity.badRequest().body("Missing 'user_Id' in request body");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        UserDetails userDetails = objectMapper.convertValue(request, UserDetails.class);

        return userDetailsService.updateUserDetailsByUserId(userId, userDetails);
    }


    // ✅ Get User Details by userId
    // URL api/user-details?userId=18
    @GetMapping
    public ResponseEntity<?> getUserDetails(@RequestParam Long userId) {
        return userDetailsService.getUserDetailsByUserId(userId);
    }


    // ✅ Delete User Details
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserDetails(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }
        return userDetailsService.deleteUserDetails(userId);
    }

}
