package com.example.HomeService.servicesinterface;

import com.example.HomeService.dto.serviceproviderdto.ServiceProviderRegisterDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderResponseDto;
import com.example.HomeService.dto.serviceproviderdto.ServiceProviderUpdateDto;
import com.example.HomeService.model.ServiceProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceProviderServiceInterface {

    List<ServiceProvider> getAllServiceProviders();

    ResponseEntity<?> registerServiceProvider(ServiceProviderRegisterDto requestDto, HttpServletResponse response, MultipartFile imageFile) throws IOException;

    ServiceProviderResponseDto getServiceProviderById(Long id);

    ServiceProviderResponseDto getServiceProviderByUserId(Long userId);

    Optional<ServiceProvider> getServiceProviderByCompanyName(String companyName);

    ResponseEntity<ServiceProviderResponseDto> updateServiceProvider(ServiceProviderUpdateDto updatedProvider, MultipartFile imageFile) throws IOException;

    String getTimeSinceJoined(Long providerId);

    ResponseEntity<Map<String, String>> deleteServiceProvider(Long providerId);
}
