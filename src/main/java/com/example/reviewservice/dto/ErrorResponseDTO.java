package com.example.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponseDTO {
    private final String code;
    private final String message;
}
