package com.example.arinternship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import com.example.arinternship.model.User;
import com.example.arinternship.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                body.get("fullname"),
                body.get("username"),
                body.get("email"),
                body.get("password"),
                body.get("role")
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "สมัครสมาชิกสำเร็จ");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error จาก validation ใน UserService
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Error อื่นๆ (เช่น database error)
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "เกิดข้อผิดพลาดในระบบ กรุณาลองใหม่อีกครั้ง");
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            User user = userService.login(body.get("username"), body.get("password"));
            
            // ถ้า login สำเร็จ (UserService จะ throw exception ถ้าล้มเหลว)
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());
            session.setAttribute("userId", user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "เข้าสู่ระบบสำเร็จ");
            response.put("role", user.getRole());
            response.put("fullname", user.getFullname());
            response.put("username", user.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // Error จาก validation ใน UserService
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Error อื่นๆ
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "เกิดข้อผิดพลาดในระบบ กรุณาลองใหม่อีกครั้ง");
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("success", true);
            response.put("user", user);
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "ไม่พบข้อมูลผู้ใช้ กรุณาเข้าสู่ระบบอีกครั้ง");
        return ResponseEntity.status(401).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        try {
            session.invalidate();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ออกจากระบบสำเร็จ");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ถ้า session หมดอายุแล้วก็ยังถือว่า logout สำเร็จ
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ออกจากระบบสำเร็จ");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * ตรวจสอบว่า username มีอยู่แล้วหรือไม่ (สำหรับ real-time validation)
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = userService.isUsernameExists(username);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}