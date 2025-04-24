package com.example.reviewservice.exception;

public class OrderServiceUnavailableException extends RuntimeException {
    public OrderServiceUnavailableException() {
        super("주문 서비스가 현재 사용 불가합니다.");
    }

    public OrderServiceUnavailableException(String message) {
        super(message);
    }

    public OrderServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
