package com.example.reviewservice.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "orderClient", url = "${order-service.url}")
public interface OrderClient {

    @GetMapping("/orders/{uid}")
    OrderDetailResponseDTO getOrderByUid(@PathVariable("uid") Integer uid);

}
