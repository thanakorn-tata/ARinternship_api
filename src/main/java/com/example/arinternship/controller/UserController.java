package com.example.arinternship.controller;

import com.example.arinternship.entity.User;
import com.example.arinternship.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        if (!isAdmin(session))
            return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "email", u.getEmail() != null ? u.getEmail() : "",
                        "fullname", u.getFullname() != null ? u.getFullname() : "",
                        "role", u.getRole() != null ? u.getRole() : "USER",
                        "active", u.getActive() != null ? u.getActive() : true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // ✅ GET MY PROFILE — user ดูข้อมูลตัวเอง
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null)
            return ResponseEntity.status(401).body("ไม่ได้ login");

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail() != null ? user.getEmail() : "");
        profile.put("fullname", user.getFullname() != null ? user.getFullname() : "");
        profile.put("role", user.getRole() != null ? user.getRole() : "USER");
        profile.put("defaultName", user.getDefaultName() != null ? user.getDefaultName() : "");
        profile.put("defaultPhone", user.getDefaultPhone() != null ? user.getDefaultPhone() : "");
        profile.put("defaultAddress", user.getDefaultAddress() != null ? user.getDefaultAddress() : "");
        return ResponseEntity.ok(profile);
    }

    // ✅ UPDATE MY PROFILE — user แก้ไขข้อมูลตัวเอง
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody Map<String, String> body,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null)
            return ResponseEntity.status(401).body("ไม่ได้ login");

        // อัปเดตเฉพาะ field ที่ส่งมา
        if (body.containsKey("email"))
            user.setEmail(body.get("email"));
        if (body.containsKey("fullname"))
            user.setFullname(body.get("fullname"));
        // ✅ บันทึกที่อยู่ default
        if (body.containsKey("defaultName"))
            user.setDefaultName(body.get("defaultName"));
        if (body.containsKey("defaultPhone"))
            user.setDefaultPhone(body.get("defaultPhone"));
        if (body.containsKey("defaultAddress"))
            user.setDefaultAddress(body.get("defaultAddress"));

        // เปลี่ยน password ถ้ามีส่งมา
        if (body.containsKey("newPassword") && !body.get("newPassword").isBlank()) {
            String current = body.get("currentPassword");
            if (current == null || current.isBlank()) {
                return ResponseEntity.badRequest().body("กรุณากรอกรหัสผ่านปัจจุบัน");
            }
            // เช็ค password ปัจจุบัน (สมมติใช้ BCrypt)
            // ถ้าไม่ได้ใช้ BCrypt ให้เปลี่ยนเป็น .equals()
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
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
                "email", user.getEmail() != null ? user.getEmail() : ""));
    }

    // ✅ TOGGLE USER ACTIVE — เฉพาะ ADMIN (ban/unban)
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleUserActive(
            @PathVariable Long id,
            HttpSession session) {
        if (!isAdmin(session))
            return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

        return userRepository.findById(id).map(user -> {
            Boolean current = user.getActive() != null ? user.getActive() : true;
            user.setActive(!current);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "active", user.getActive(),
                    "message", user.getActive() ? "เปิดใช้งานแล้ว" : "ระงับบัญชีแล้ว"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ CHANGE ROLE — เฉพาะ ADMIN
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {
        if (!isAdmin(session))
            return ResponseEntity.status(403).body("ไม่มีสิทธิ์");

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
                    "message", "เปลี่ยน role เป็น " + newRole + " แล้ว"));
        }).orElse(ResponseEntity.notFound().build());
    }
    // ✅ เพิ่ม method นี้ใน UserController.java

    @PutMapping("/{id}")
    public ResponseEntity<?> editUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {
        User admin = (User) session.getAttribute("USER");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return ResponseEntity.status(403).body("ไม่มีสิทธิ์");
        }

        return userRepository.findById(id).map(user -> {
            if (body.containsKey("username") && !body.get("username").isBlank()) {
                user.setUsername(body.get("username").trim());
            }
            if (body.containsKey("email")) {
                user.setEmail(body.get("email").trim());
            }
            if (body.containsKey("fullname")) {
                user.setFullname(body.get("fullname").trim());
            }
            if (body.containsKey("password") && !body.get("password").isBlank()) {
                user.setPassword(body.get("password").trim()); // ถ้ามี BCrypt ให้ encode ด้วย
            }
            userRepository.save(user);

            Map<String, Object> res = new HashMap<>();
            res.put("id", user.getId());
            res.put("username", user.getUsername());
            res.put("email", user.getEmail());
            res.put("fullname", user.getFullname());
            res.put("message", "แก้ไขข้อมูลสำเร็จ");
            return ResponseEntity.ok(res);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ GET DEFAULT ADDRESS — สำหรับ checkout prefill
    @GetMapping("/me/address")
    public ResponseEntity<?> getDefaultAddress(HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        // โหลดข้อมูลล่าสุดจาก DB
        return userRepository.findById(user.getId()).map(u -> {
            Map<String, Object> addr = new HashMap<>();
            addr.put("defaultName", u.getDefaultName() != null ? u.getDefaultName() : "");
            addr.put("defaultPhone", u.getDefaultPhone() != null ? u.getDefaultPhone() : "");
            addr.put("defaultAddress", u.getDefaultAddress() != null ? u.getDefaultAddress() : "");
            return ResponseEntity.ok(addr);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ SAVE DEFAULT ADDRESS — บันทึกที่อยู่ default
    @PutMapping("/me/address")
    public ResponseEntity<?> saveDefaultAddress(
            @RequestBody Map<String, String> body,
            HttpSession session) {
        User user = getSessionUser(session);
        if (user == null) return ResponseEntity.status(401).body("ไม่ได้ login");

        return userRepository.findById(user.getId()).map(u -> {
            if (body.containsKey("defaultName"))
                u.setDefaultName(body.get("defaultName"));
            if (body.containsKey("defaultPhone"))
                u.setDefaultPhone(body.get("defaultPhone"));
            if (body.containsKey("defaultAddress"))
                u.setDefaultAddress(body.get("defaultAddress"));
            userRepository.save(u);
            session.setAttribute("USER", u);
            return ResponseEntity.ok(Map.of("message", "บันทึกที่อยู่สำเร็จ"));
        }).orElse(ResponseEntity.notFound().build());
    }
}