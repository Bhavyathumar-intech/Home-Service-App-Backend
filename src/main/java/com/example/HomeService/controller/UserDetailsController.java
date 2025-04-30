package com.example.HomeService.controller;

import com.example.HomeService.dto.userdetailsdto.UserDetailsRegisterDto;
import com.example.HomeService.model.UserDetails;
import com.example.HomeService.model.Users;
import com.example.HomeService.service.UserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
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

    /**
     * Registers user details along with the user's image.
     *
     * @param userDetailsRegisterDtoString The JSON string containing user details data.
     * @param imageFile The image file of the user.
     * @return A ResponseEntity containing the status of the operation.
     * @throws IOException If there is an error during the parsing of the JSON or file operations.
     */
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

    /**
     * Updates existing user details along with an optional image.
     *
     * @param userDetailsUpdate The updated user details in JSON format.
     * @param imageFile An optional image file to update the user's image.
     * @return A ResponseEntity indicating the result of the update operation.
     * @throws IOException If there is an error during JSON parsing or file operations.
     */
    @PatchMapping("/update")
    public ResponseEntity<?> updateUserDetails(
            @RequestPart("UserDetailsRegisterDto") String userDetailsUpdate,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        if (userDetailsUpdate == null || userDetailsUpdate.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User details data cannot be null or empty.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDetailsRegisterDto userDetailsUpdateDto = objectMapper.readValue(userDetailsUpdate, UserDetailsRegisterDto.class);
            UserDetails userDetails = convertToEntity(userDetailsUpdateDto);

            if (userDetails.getUser() == null || userDetails.getUser().getId() == null) {
                return ResponseEntity.badRequest().body("Missing 'userId' in request body.");
            }

            return userDetailsService.updateUserDetailsByUserId(userDetails.getUser().getId(), userDetails, imageFile);

        } catch (JsonMappingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format or structure in user details.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error processing JSON.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("I/O error occurred while processing the request.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Optional: add a custom message
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Retrieves the details of a user by their user ID.
     *
     * @param userId The user ID whose details need to be fetched.
     * @return A ResponseEntity with the user details if found.
     */
    @GetMapping("/get-details-by-id/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        return userDetailsService.getUserDetailsByUserId(userId);
    }

    /**
     * Deletes the user details for a specific user ID.
     *
     * @param userDetailsRegisterDto The DTO containing the userId to delete.
     * @return A ResponseEntity indicating the result of the deletion operation.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserDetails(@RequestBody UserDetailsRegisterDto userDetailsRegisterDto) {
        if (userDetailsRegisterDto.getUserId() == null) {
            return ResponseEntity.badRequest().body("Missing 'userId' in request body");
        }

        return userDetailsService.deleteUserDetails(userDetailsRegisterDto.getUserId());
    }

    /**
     * Converts a UserDetailsRegisterDto to a UserDetails entity.
     *
     * @param dto The DTO containing user details.
     * @return A UserDetails entity.
     */
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

    /**
     * Retrieves an image based on its filename.
     *
     * @param filename The name of the image file.
     * @return A ResponseEntity containing the image or a not-found response if the image does not exist.
     * @throws MalformedURLException If the URL of the image path is malformed.
     */
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
