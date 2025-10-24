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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    @Transactional
    public void register(RegisterRequest request) {
        // Check if username or email exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("ชื่อผู้ใช้นี้มีอยู่แล้ว");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("อีเมลนี้มีอยู่แล้ว");
        }
        
        // Create new user
        User user = new User();
        user.setFullname(request.getFullname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        user.setActive(true);
        
        userRepository.save(user);
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Authenticate
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate token
            String token = jwtService.generateToken(userDetails, user.getId(), user.getRole().name());
            String refreshToken = jwtService.generateToken(userDetails, user.getId(), user.getRole().name());
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Log successful login
            logLogin(user.getId(), true, httpRequest);
            
            // Return response
            LoginResponse.UserDTO userDTO = new LoginResponse.UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFullname(),
                user.getEmail(),
                user.getRole().name()
            );
            
            return new LoginResponse(true, token, refreshToken, userDTO);
            
        } catch (Exception e) {
            // Log failed login
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null) {
                logLogin(user.getId(), false, httpRequest);
            }
            throw new RuntimeException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
        }
    }
    
    private void logLogin(Long userId, Boolean success, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setSuccess(success);
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        loginLogRepository.save(log);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}