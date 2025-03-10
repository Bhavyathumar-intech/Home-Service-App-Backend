package com.example.HomeService.service;

import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.repo.UserDetailsRepository;
import com.example.HomeService.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository usersRepository;

    // ✅ Save or Update UserDetails
    public ResponseEntity<?> saveOrUpdateUserDetails(Long userId, UserDetails userDetails) {
        Optional<Users> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Users user = userOptional.get();
        userDetails.setUser(user); // ✅ Manually set the user object

        UserDetails savedDetails = userDetailsRepository.save(userDetails);
        return ResponseEntity.ok(savedDetails);
    }

    public ResponseEntity<?> updateUserDetailsByUserId(Long userId, UserDetails updatedDetails) {
        Optional<UserDetails> existingDetailsOpt = userDetailsRepository.findByUserId(userId);

        if (existingDetailsOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }

        UserDetails existingDetails = existingDetailsOpt.get();

        existingDetails.setAddress(updatedDetails.getAddress());
        existingDetails.setCity(updatedDetails.getCity());
        existingDetails.setState(updatedDetails.getState());
        existingDetails.setCountry(updatedDetails.getCountry());
        existingDetails.setZipCode(updatedDetails.getZipCode());
        existingDetails.setDateOfBirth(updatedDetails.getDateOfBirth());
        existingDetails.setProfilePictureUrl(updatedDetails.getProfilePictureUrl());

        userDetailsRepository.save(existingDetails);

        return ResponseEntity.ok("User details updated successfully!");
    }

    // ✅ Get UserDetails by userId
    public ResponseEntity<?> getUserDetailsByUserId(Long userId) {
        Optional<UserDetails> userDetailsOptional = userDetailsRepository.findByUserId(userId);

        if (userDetailsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }

        return ResponseEntity.ok(userDetailsOptional.get());
    }

    // ✅ Delete UserDetails
    public ResponseEntity<?> deleteUserDetails(Long userId) {
        if (!userDetailsRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }

        userDetailsRepository.deleteById(userId);
        return ResponseEntity.ok("User details deleted successfully");
    }
}
