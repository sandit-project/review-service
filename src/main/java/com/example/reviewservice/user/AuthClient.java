package com.example.reviewservice.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authClient", url = "${auth-service.url}")
public interface AuthClient {

    @PostMapping("/auths/user/info")
    UserInfoResponseDTO getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
