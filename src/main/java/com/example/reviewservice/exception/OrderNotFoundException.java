package com.example.reviewservice.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Integer orderUid) {
        super("주문을 찾을 수 없습니다. orderUid=" + orderUid);
    }
}
