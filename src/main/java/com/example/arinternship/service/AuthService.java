package com.example.arinternship.service;

import com.example.arinternship.dto.LoginRequest;
import com.example.arinternship.dto.LoginResponse;
import com.example.arinternship.dto.RegisterRequest;
import com.example.arinternship.entity.LoginLog;
import com.example.arinternship.entity.User;
import com.example.arinternship.repository.LoginLogRepository;
import com.example.arinternship.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    /**
     * ลงทะเบียนผู้ใช้ใหม่
     */
    @Transactional
    public void register(RegisterRequest request) {
        log.info("📝 Attempting to register user: {}", request.getUsername());
        
        try {
            // ตรวจสอบว่า username ซ้ำหรือไม่
            if (userRepository.existsByUsername(request.getUsername())) {
                log.warn("⚠️ Username already exists: {}", request.getUsername());
                throw new RuntimeException("ชื่อผู้ใช้นี้มีอยู่แล้ว");
            }
            
            // ตรวจสอบว่า email ซ้ำหรือไม่
            if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("⚠️ Email already exists: {}", request.getEmail());
                throw new RuntimeException("อีเมลนี้มีอยู่แล้ว");
            }
            
            // สร้าง user ใหม่
            User user = new User();
            user.setFullname(request.getFullname());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(User.Role.USER);
            user.setActive(true);
            
            // บันทึกลง database
            User savedUser = userRepository.save(user);
            log.info("✅ User registered successfully: {} (ID: {})", 
                savedUser.getUsername(), savedUser.getId());
            
        } catch (RuntimeException e) {
            // ถ้าเป็น RuntimeException ที่เรา throw เอง ให้ throw ต่อไป
            throw e;
        } catch (Exception e) {
            // ถ้าเป็น error อื่นๆ (เช่น database error)
            log.error("❌ Error during registration: ", e);
            throw new RuntimeException("เกิดข้อผิดพลาดในการลงทะเบียน: " + e.getMessage());
        }
    }
    
    /**
     * เข้าสู่ระบบ
     */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("🔐 Attempting login for user: {}", request.getUsername());
        
        try {
            // Authenticate
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            log.debug("✅ Authentication successful for: {}", request.getUsername());
            
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate tokens
            String token = jwtService.generateToken(userDetails, user.getId(), user.getRole().name());
            String refreshToken = jwtService.generateToken(userDetails, user.getId(), user.getRole().name());
            
            log.debug("✅ JWT tokens generated for: {}", request.getUsername());
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Log successful login
            logLogin(user.getId(), true, httpRequest);
            
            log.info("✅ Login successful for user: {} (ID: {})", user.getUsername(), user.getId());
            
            // Return response
            LoginResponse.UserDTO userDTO = new LoginResponse.UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullname(),
                user.getEmail(),
                user.getRole().name()
            );
            
            return new LoginResponse(true, token, refreshToken, userDTO);
            
        } catch (BadCredentialsException e) {
            log.warn("⚠️ Bad credentials for user: {}", request.getUsername());
            
            // Log failed login
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null) {
                logLogin(user.getId(), false, httpRequest);
            }
            
            throw new RuntimeException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            
        } catch (Exception e) {
            log.error("❌ Login error for user {}: ", request.getUsername(), e);
            
            throw new RuntimeException("เกิดข้อผิดพลาดในการเข้าสู่ระบบ");
        }
    }
    
    /**
     * บันทึก login log
     */
    private void logLogin(Long userId, Boolean success, HttpServletRequest request) {
        try {
            LoginLog log = new LoginLog();
            log.setUserId(userId);
            log.setSuccess(success);
            log.setIpAddress(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            loginLogRepository.save(log);
            
            this.log.debug("📊 Login log saved for user ID: {}, success: {}", userId, success);
        } catch (Exception e) {
            // ไม่ให้ error ใน logging ทำให้ login ล้มเหลว
            this.log.error("❌ Failed to save login log: ", e);
        }
    }
    
    /**
     * ดึง IP address ของ client
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}