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

    @PostMapping("/addDetails")
    public ResponseEntity<?> registerUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);
        return userDetailsService.saveOrUpdateUserDetails(userDetailsRegisterDto.getUserId(), userDetails);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        if (userDetailsRegisterDto.getUserId() == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }

        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);
        return userDetailsService.updateUserDetailsByUserId(userDetailsRegisterDto.getUserId(), userDetails);
    }

    @GetMapping("/get-details-by-id/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
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