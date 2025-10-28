package com.example.arinternship.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ปิด CSRF เพื่อให้ Angular ส่ง cookie ได้
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // อนุญาตทุก API
            );
        return http.build();
    }
}
