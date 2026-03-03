package com.example.arinternship.service;

import com.example.arinternship.dto.RegisterRequest;
import com.example.arinternship.dto.UserResponse;
import com.example.arinternship.entity.User;
import com.example.arinternship.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ LOGIN - คืน User entity (เก็บ session ใน controller)
    public User login(String username, String password) {
        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            // ✅ ใช้ error เดียวกันทั้ง user not found และ wrong password
            //    เพื่อป้องกัน User Enumeration Attack
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        return user;
    }

    // ✅ REGISTER
    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("username ซ้ำ");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        user.setRole("USER");

        userRepository.save(user);
    }
}