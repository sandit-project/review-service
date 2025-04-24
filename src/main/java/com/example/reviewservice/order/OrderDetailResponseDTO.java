package com.example.reviewservice.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderDetailResponseDTO {
    private Integer uid;
    private Integer userUid;
    private Integer storeUid;
    private List<CartItemDTO> items;
    private String payment;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime reservationDate;
}
