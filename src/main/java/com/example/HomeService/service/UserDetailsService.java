package com.example.HomeService.service;

import com.example.HomeService.dto.userDetailsDto.UserDetailsResponseDTO;
import com.example.HomeService.dto.userDetailsDto.UserDetailsRegisterDto;
import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.repo.UserDetailsRepository;
import com.example.HomeService.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository usersRepository;

    private static final String IMAGE_DIRECTORY = "D:\\Project\\Home-Service-App-Backend\\src\\Image\\";

    private String storeImage(MultipartFile file) throws IOException {
        // Ensure directory exists
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Generate a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_DIRECTORY, fileName);
        // Save file to disk
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    public ResponseEntity<?> saveUserDetails(Long userId, UserDetails userDetails, MultipartFile imageFile) throws IOException {
        System.out.println(userDetails);
        Optional<Users> userOptional = usersRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userDetails.setUser(userOptional.get());
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeImage(imageFile);
            userDetails.setProfilePictureUrl(fileName);
        }
        UserDetails savedDetails = userDetailsRepository.save(userDetails);
        return ResponseEntity.ok(convertToDto(savedDetails));
    }

    public ResponseEntity<?> getUserDetailsByUserId(Long userId) {
        UserDetails userDetailsOptional = userDetailsRepository.findByUserId(userId).get();

        if (userDetailsOptional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        System.out.println("UserDetails OPTIONAL     " + userDetailsOptional);

        // Convert Entity to DTO
        UserDetailsResponseDTO userDetailsResponseDTO = new UserDetailsResponseDTO(userDetailsOptional);
        System.out.println("UserDetails DTO  " + userDetailsResponseDTO.toString());
        return ResponseEntity.ok(userDetailsResponseDTO);
    }


    public ResponseEntity<?> updateUserDetailsByUserId(Long userId, UserDetails updatedDetails, MultipartFile imageFile) throws IOException {
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
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeImage(imageFile);
            existingDetails.setProfilePictureUrl(fileName);
        }
//        existingDetails.setProfilePictureUrl(updatedDetails.getProfilePictureUrl());

        UserDetails savedDetails = userDetailsRepository.save(existingDetails);
        return ResponseEntity.ok(convertToDto(savedDetails));
    }


    public ResponseEntity<?> deleteUserDetails(Long userId) {
        Optional<UserDetails> userDetailsOptional = userDetailsRepository.findByUser_Id(userId);
        if (userDetailsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found for userId: " + userId);
        }

        userDetailsRepository.delete(userDetailsOptional.get());
        return ResponseEntity.ok("User details deleted successfully for userId: " + userId);
    }

    private UserDetailsResponseDTO convertToDto(UserDetails userDetails) {
        return new UserDetailsResponseDTO(userDetails);
//                userDetails.getUdId(),
//                userDetails.getUser().getId(),
//                userDetails.getUser().getName(),
//                userDetails.getUser().getEmail(),
//                userDetails.getUser().getPhoneNumber(),
//                userDetails.getAddress(),
//                userDetails.getCity(),
//                userDetails.getState(),
//                userDetails.getCountry(),
//                userDetails.getZipCode(),
//                userDetails.getDateOfBirth(),
//                userDetails.getProfilePictureUrl()
//        );
    }

}
