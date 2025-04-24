package com.example.reviewservice.order;

import lombok.Getter;

@Getter
public class CartItemDTO {
    private Long uid;
    private String menuName;
    private int amount;
    private Long totalPrice;
    private Double calorie;
    private Long unitPrice;
}
