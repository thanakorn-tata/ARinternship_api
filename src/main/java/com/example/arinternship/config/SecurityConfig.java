package com.example.arinternship.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ❌ ปิด csrf (จำเป็นมากสำหรับ Angular)
            .csrf(csrf -> csrf.disable())

            // ✅ เปิด cors
            .cors(Customizer.withDefaults())

            // ✅ ตั้ง rule การเข้าถึง
            .authorizeHttpRequests(auth -> auth
                // 🔓 auth APIs
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/logout",
                    "/api/auth/me"
                ).permitAll()

                // 🔓 static / public
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/assets/**"
                ).permitAll()

                // 🔐 ที่เหลือต้อง login
                .anyRequest().authenticated()
            )

            // ❌ ไม่ใช้ form login ของ spring
            .formLogin(form -> form.disable())

            // ❌ ไม่ใช้ basic auth
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}