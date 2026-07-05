package com.example.arinternship.controller;

import com.example.arinternship.dto.LoginRequest;
import com.example.arinternship.dto.RegisterRequest;
import com.example.arinternship.entity.User;
import com.example.arinternship.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = "http://localhost:4200",
    allowCredentials = "true"
)
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // ✅ LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody LoginRequest req,
        HttpSession session
    ) {
        User user = userRepository
            .findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("user not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("password ไม่ถูกต้อง");
        }

        // ✅ เช็ค active ก่อน login
        if (!Boolean.TRUE.equals(user.getActive())) {
            return ResponseEntity.status(403).body("บัญชีนี้ถูกระงับการใช้งาน กรุณาติดต่อแอดมิน");
        }

        // ✅ เก็บ session
        session.setAttribute("USER", user);
        session.setAttribute("ROLE", user.getRole());

        return ResponseEntity.ok(user);
    }

    // =========================
    // ✅ REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("username ซ้ำ");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // =========================
    // ✅ ME (เช็ค session)
    // =========================
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        return ResponseEntity.ok(session.getAttribute("USER"));
    }

    // =========================
    // ✅ LOGOUT
    // =========================
    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }
}