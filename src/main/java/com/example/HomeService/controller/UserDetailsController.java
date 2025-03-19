//package com.example.HomeService.controller;
//
//import com.example.HomeService.model.UserDetails;
//import com.example.HomeService.model.Users;
//import com.example.HomeService.service.UserDetailsService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//@RestController
//@CrossOrigin
//@RequestMapping("/api/user-details")
//public class UserDetailsController {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @PostMapping
//    public ResponseEntity<?> registerUserDetails(@RequestBody Map<String, Object> userDetailsMap) {
//        UserDetails userDetails = new UserDetails();
//
//        // Handling user_Id correctly
//        if (userDetailsMap.get("userId") != null) {
//            Users user = new Users();
//            user.setId(Long.parseLong(userDetailsMap.get("userId").toString())); // Safe conversion
//            System.out.println(user.toString()); //debugging
//            userDetails.setUser(user);
//        }
//
//        userDetails.setAddress((String) userDetailsMap.get("address"));
//        userDetails.setCity((String) userDetailsMap.get("city"));
//        userDetails.setState((String) userDetailsMap.get("state"));
//        userDetails.setCountry((String) userDetailsMap.get("country"));
//        userDetails.setZipCode((String) userDetailsMap.get("zipCode"));
//
//        // Parse dateOfBirth safely
//        userDetails.setDateOfBirth(LocalDate.parse((String) userDetailsMap.get("dateOfBirth")));
//
//        userDetails.setProfilePictureUrl((String) userDetailsMap.get("profilePictureUrl"));
//
//        return userDetailsService.saveOrUpdateUserDetails(userDetails.getUser().getId(), userDetails);
//    }
//
//
//    // ✅ PUT request for updating existing user details (User ID required in JSON)
/// /    @PutMapping
/// /    public ResponseEntity<?> updateUserDetails(@RequestBody Map<String, Object> request) {
/// /        Long userId = request.get("userId") instanceof Number ?
/// /                ((Number) request.get("userId")).longValue() : null;
/// /
/// /        if (userId == null) {
/// /            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
/// /        }
/// /
/// /        ObjectMapper objectMapper = new ObjectMapper();
/// /        UserDetails userDetails = objectMapper.convertValue(request, UserDetails.class);
/// /
/// /        return userDetailsService.updateUserDetailsByUserId(userId, userDetails);
/// /    }
//
//    @PutMapping
//    public ResponseEntity<?> updateUserDetails(@RequestBody Map<String, Object> request) {
//        Long userId = request.get("userId") instanceof Number ?
//                ((Number) request.get("userId")).longValue() : null;
//
//        if (userId == null) {
//            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
//        }
//
//        // Remove `userId` from request before converting
//        request.remove("userId");
//
//        UserDetails userDetails = new UserDetails();
//
//        // Handle nested `userId` inside `Users`
//        Users user = new Users();
//        user.setId(userId);
//        userDetails.setUser(user);
//
//        userDetails.setAddress((String) request.get("address"));
//        userDetails.setCity((String) request.get("city"));
//        userDetails.setState((String) request.get("state"));
//        userDetails.setCountry((String) request.get("country"));
//        userDetails.setZipCode((String) request.get("zipCode"));
//
//        // ✅ Safely parse dateOfBirth from String
//        if (request.get("dateOfBirth") != null) {
//            userDetails.setDateOfBirth(LocalDate.parse(request.get("dateOfBirth").toString()));
//        }
//
//        userDetails.setProfilePictureUrl((String) request.get("profilePictureUrl"));
//
//        return userDetailsService.updateUserDetailsByUserId(userId, userDetails);
//    }
//
//
//    // ✅ Get User Details by userId
//    // URL api/user-details?userId=18
//    @GetMapping
//    public ResponseEntity<?> getUserDetails(@RequestParam Long userId) {
//        return userDetailsService.getUserDetailsByUserId(userId);
//    }
//
//
//    //    // ✅ Delete User Details
////    @DeleteMapping("/delete")
////    public ResponseEntity<?> deleteUserDetails(@RequestBody Map<String, Long> request) {
////        Long userId = request.get("userId");
////        System.out.println(userId);
////        if (userId == null) {
////            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
////        }
////        return userDetailsService.deleteUserDetails(userId);
////    }
//    @DeleteMapping("/delete")
//    public ResponseEntity<?> deleteUserDetails(@RequestBody Map<String, Long> request) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        Long userId = objectMapper.convertValue(request.get("userId"), Long.class);
//
//        if (userId == null) {
//            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
//        }
//
//        System.out.println("userId: " + userId);
//
//        return userDetailsService.deleteUserDetails(userId);
//    }
//
//}

package com.example.HomeService.controller;

import com.example.HomeService.dto.userDetailsDto.UserDetailsRegisterDto;
import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/user-details")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<?> registerUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);
        return userDetailsService.saveOrUpdateUserDetails(userDetailsRegisterDto.getUserId(), userDetails);
    }

    @PutMapping
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        if (userDetailsRegisterDto.getUserId() == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }

        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);
        return userDetailsService.updateUserDetailsByUserId(userDetailsRegisterDto.getUserId(), userDetails);
    }

    @GetMapping
    public ResponseEntity<?> getUserDetails(@RequestParam Long userId) {
        return userDetailsService.getUserDetailsByUserId(userId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        if (userDetailsRegisterDto.getUserId() == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }

        return userDetailsService.deleteUserDetails(userDetailsRegisterDto.getUserId());
    }

    private UserDetails convertToEntity(UserDetailsRegisterDto dto) {
        UserDetails userDetails = new UserDetails();
        Users user = new Users();
        user.setId(dto.getUserId());
        userDetails.setUser(user);
        userDetails.setAddress(dto.getAddress());
        userDetails.setCity(dto.getCity());
        userDetails.setState(dto.getState());
        userDetails.setCountry(dto.getCountry());
        userDetails.setZipCode(dto.getZipCode());
        userDetails.setDateOfBirth(dto.getDateOfBirth());
        userDetails.setProfilePictureUrl(dto.getProfilePictureUrl());
        return userDetails;
    }
}