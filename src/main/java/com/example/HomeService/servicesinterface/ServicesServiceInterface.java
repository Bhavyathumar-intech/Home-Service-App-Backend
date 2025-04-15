package com.example.HomeService.servicesinterface;

import com.example.HomeService.dto.servicesdto.ServicesRegisterDto;
import com.example.HomeService.dto.servicesdto.ServicesResponseDto;
import com.example.HomeService.dto.servicesdto.ServicesUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ServicesServiceInterface {

    ResponseEntity<?> createService(ServicesRegisterDto dto, MultipartFile imageFile) throws IOException;

    List<ServicesResponseDto> getAllServices();

    Optional<ServicesResponseDto> getServiceById(Long id);

    ResponseEntity<ServicesResponseDto> updateService(Long id, ServicesUpdateDto dto, MultipartFile imageFile) throws IOException;

    void deleteService(Long id);

    List<ServicesResponseDto> getServicesByServiceProviderId(Long serviceProviderId);
}
