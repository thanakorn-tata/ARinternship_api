package com.example.arinternship.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// ✅ การตั้งค่า CORS ทั้งหมดย้ายไปรวมอยู่ที่ CorsConfig.java (Bean เดียว) แล้ว
// ไม่ต้องตั้งซ้ำที่นี่ เพราะถ้ามี 2 แหล่งพร้อมกัน จะทำให้ browser เจอ
// Access-Control-Allow-Origin ซ้ำกัน 2 ค่า แล้ว reject request ทั้งที่ config ถูกทั้งคู่
@Configuration
public class WebConfig implements WebMvcConfigurer {
}