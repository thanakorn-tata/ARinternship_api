package com.example.arinternship.controller;

import com.example.arinternship.entity.User;
import com.example.arinternship.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("USER");
    }

    private boolean isAdmin(HttpSession session) {
        User user = getSessionUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }

    // ✅ GET ALL USERS — เฉพาะ ADMIN
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        List<Map<String, Object>> users = userRepository.findAll()
            .stream()
            .map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail() != null ? u.getEmail() : "",
                "fullname", u.getFullname() != null ? u.getFullname() : "",
                "role", u.getRole() != null ? u.getRole() : "USER",
                "active", u.getActive() != null ? u.getActive() : true
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // ✅ GET MY PROFILE — user ดูข้อมูลตัวเอง
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail() != null ? user.getEmail() : "",
            "fullname", user.getFullname() != null ? user.getFullname() : "",
            "role", user.getRole() != null ? user.getRole() : "USER"
        ));
    }

    // ✅ UPDATE MY PROFILE — user แก้ไขข้อมูลตัวเอง
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
        @RequestBody Map<String, String> body,
        HttpSession session
    ) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        // อัปเดตเฉพาะ field ที่ส่งมา
        if (body.containsKey("email"))    user.setEmail(body.get("email"));
        if (body.containsKey("fullname")) user.setFullname(body.get("fullname"));

        // เปลี่ยน password ถ้ามีส่งมา
        if (body.containsKey("newPassword") && !body.get("newPassword").isBlank()) {
            String current = body.get("currentPassword");
            if (current == null || current.isBlank()) {
                return ResponseEntity.badRequest().body("กรุณากรอกรหัสผ่านปัจจุบัน");
            }
            // เช็ค password ปัจจุบัน (สมมติใช้ BCrypt)
            // ถ้าไม่ได้ใช้ BCrypt ให้เปลี่ยนเป็น .equals()
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            if (!encoder.matches(current, user.getPassword())) {
                return ResponseEntity.badRequest().body("รหัสผ่านปัจจุบันไม่ถูกต้อง");
            }
            user.setPassword(encoder.encode(body.get("newPassword")));
        }

        userRepository.save(user);

        // อัปเดต session ด้วย
        session.setAttribute("USER", user);

        return ResponseEntity.ok(Map.of(
            "message", "อัปเดตข้อมูลสำเร็จ",
            "fullname", user.getFullname() != null ? user.getFullname() : "",
            "email", user.getEmail() != null ? user.getEmail() : ""
        ));
    }

    // ✅ TOGGLE USER ACTIVE — เฉพาะ ADMIN (ban/unban)
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleUserActive(
        @PathVariable Long id,
        HttpSession session
    ) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        return userRepository.findById(id).map(user -> {
            Boolean current = user.getActive() != null ? user.getActive() : true;
            user.setActive(!current);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "active", user.getActive(),
                "message", user.getActive() ? "เปิดใช้งานแล้ว" : "ระงับบัญชีแล้ว"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ CHANGE ROLE — เฉพาะ ADMIN
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(
        @PathVariable Long id,
        @RequestBody Map<String, String> body,
        HttpSession session
    ) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        String newRole = body.get("role");
        if (!"USER".equals(newRole) && !"ADMIN".equals(newRole)) {
            return ResponseEntity.badRequest().body("role ต้องเป็น USER หรือ ADMIN เท่านั้น");
        }

        return userRepository.findById(id).map(user -> {
            user.setRole(newRole);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "role", user.getRole(),
                "message", "เปลี่ยน role เป็น " + newRole + " แล้ว"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}