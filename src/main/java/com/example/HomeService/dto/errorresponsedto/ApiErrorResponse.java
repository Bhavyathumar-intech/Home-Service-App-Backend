package com.example.HomeService.dto.errorresponsedto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private int status;
}