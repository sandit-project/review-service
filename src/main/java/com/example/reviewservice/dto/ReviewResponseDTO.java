package com.example.reviewservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDTO {
    private boolean isSuccess;
    private String message;
}
