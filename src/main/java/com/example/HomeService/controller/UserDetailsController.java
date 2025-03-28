package com.example.HomeService.controller;

import com.example.HomeService.dto.userDetailsDto.UserDetailsRegisterDto;
import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
@RequestMapping("/api/user-details")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/addDetails")
    public ResponseEntity<?> registerUserDetails(
            @RequestPart("UserDetailsRegisterDto") String userDetailsRegisterDtoString,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {

        // Convert JSON String to UserDetailsRegisterDto Object
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetailsRegisterDto userDetailsRegisterDto = objectMapper.readValue(userDetailsRegisterDtoString, UserDetailsRegisterDto.class);

        // Convert DTO to Entity
        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);

        return userDetailsService.saveOrUpdateUserDetails(userDetailsRegisterDto.getUserId(), userDetails, imageFile);
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
        return userDetails;
    }

    // Send that uuid of image on this route to get Image
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\Image\\" + filename);
        Resource resource = new UrlResource(imagePath.toUri());
        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Change if using PNG, etc.
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}