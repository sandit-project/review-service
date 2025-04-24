package com.example.reviewservice.exception;

import com.example.reviewservice.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleOrderNotFound(OrderNotFoundException ex) {
        log.warn("OrderNotFoundException: {}", ex.getMessage());
        return ErrorResponseDTO.builder()
                .code("ORDER_NOT_FOUND")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(OrderServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponseDTO handleOrderSvcDown(OrderServiceUnavailableException ex) {
        log.error("OrderServiceUnavailableException: {}", ex.getMessage());
        return ErrorResponseDTO.builder()
                .code("ORDER_SERVICE_DOWN")
                .message("주문 서비스가 현재 사용 불가합니다.")
                .build();
    }

}
