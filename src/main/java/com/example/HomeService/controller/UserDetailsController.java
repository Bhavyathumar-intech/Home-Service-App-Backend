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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin
@RequestMapping("/api/user-details")
@PreAuthorize("hasRole('USER')")
public class UserDetailsController {

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/add-details")
    public ResponseEntity<?> registerUserDetails(
            @RequestPart("UserDetailsRegisterDto") String userDetailsRegisterDtoString,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {

        // Convert JSON String to UserDetailsRegisterDto Object
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetailsRegisterDto userDetailsRegisterDto = objectMapper.readValue(userDetailsRegisterDtoString, UserDetailsRegisterDto.class);

        // Convert DTO to Entity
        UserDetails userDetails = convertToEntity(userDetailsRegisterDto);

        return userDetailsService.saveUserDetails(userDetailsRegisterDto.getUserId(), userDetails, imageFile);
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUserDetails(
            @RequestPart("UserDetailsRegisterDto") String userDetailsUpdate,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        // Convert JSON String to UserDetailsRegisterDto Object
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetailsRegisterDto userDetailsUpdateDto = objectMapper.readValue(userDetailsUpdate, UserDetailsRegisterDto.class);

        UserDetails userDetails = convertToEntity(userDetailsUpdateDto);

        if (userDetails.getUser().getId() == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }

        return userDetailsService.updateUserDetailsByUserId(userDetails.getUser().getId(), userDetails, imageFile);
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
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path imagePath = Paths.get("D:\\Project\\Home-Service-App-Backend\\src\\UserDetailsImage\\" + filename);
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Get file extension
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        // Determine media type based on extension
        MediaType mediaType;
        switch (fileExtension) {
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "bmp":
                mediaType = MediaType.parseMediaType("image/bmp");
                break;
            case "webp":
                mediaType = MediaType.parseMediaType("image/webp");
                break;
            case "jpg":
            case "jpeg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM; // Fallback for unknown types
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}