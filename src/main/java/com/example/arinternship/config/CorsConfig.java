package com.example.arinternship.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // ✅ เปลี่ยนจาก CorsFilter เป็น CorsConfigurationSource แบบ Bean ตรงๆ
    // เพราะ SecurityConfig ใช้ .cors(Customizer.withDefaults()) ซึ่งจะไปหา Bean
    // ชนิด CorsConfigurationSource มาใช้เอง ถ้าไม่ประกาศแบบนี้ Spring Security
    // จะมองไม่เห็น config ที่เราตั้งไว้ แล้ว block preflight request (OPTIONS) ด้วย 403
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ ต้องระบุ origin ตรงๆ ห้ามใช้ * เมื่อ allowCredentials = true
        // เก็บ localhost ไว้ด้วยสำหรับตอน dev บนเครื่องตัวเอง + เพิ่ม domain จริงของ Vercel
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://final-numprig.vercel.app"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // ✅ อนุญาต cookie ข้าม origin
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}