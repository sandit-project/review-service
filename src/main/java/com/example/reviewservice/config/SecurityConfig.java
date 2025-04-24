package com.example.reviewservice.config;

import com.example.reviewservice.token.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // 1) 공개(조회) 엔드포인트
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()

                        // 2) 작성은 USER
                        .requestMatchers(HttpMethod.POST, "/reviews").hasRole("USER")

                        // 3) 삭제는 USER or ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**")
                        .hasAnyRole("USER","ADMIN")

                        // 그 외 나머지는 모두 인증 필요
                        .anyRequest().authenticated()
                )

                // 4) 필터 체인 앞부분에 JwtAuthenticationFilter 삽입
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

