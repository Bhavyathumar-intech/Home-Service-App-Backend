package com.example.HomeService.servicesinterface;

import com.example.HomeService.dto.userdetailsdto.UserDetailsResponseDTO;
import com.example.HomeService.model.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserDetailsServicesInterface {

    ResponseEntity<?> saveUserDetails(Long userId, UserDetails userDetails, MultipartFile imageFile) throws IOException;

    ResponseEntity<?> getUserDetailsByUserId(Long userId);

    ResponseEntity<?> updateUserDetailsByUserId(Long userId, UserDetails updatedDetails, MultipartFile imageFile) throws IOException;

    ResponseEntity<?> deleteUserDetails(Long userId);
}
