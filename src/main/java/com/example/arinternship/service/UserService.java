package com.example.arinternship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.arinternship.model.User;
import com.example.arinternship.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * เข้ารหัสรหัสผ่าน
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return encoder.encode(rawPassword);
    }

    /**
     * ตรวจสอบรหัสผ่าน
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * ลงทะเบียนผู้ใช้ใหม่
     */
    public User register(String fullname, String username, String email, String password, String role) {
        // ตรวจสอบข้อมูลที่จำเป็น
        if (fullname == null || fullname.trim().isEmpty()) {
            throw new IllegalArgumentException("ชื่อ-นามสกุลไม่สามารถเว้นว่างได้");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ชื่อผู้ใช้ไม่สามารถเว้นว่างได้");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("อีเมลไม่สามารถเว้นว่างได้");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("รหัสผ่านต้องมีความยาวอย่างน้อย 6 ตัวอักษร");
        }

        // ตรวจสอบว่า username ซ้ำหรือไม่
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("ชื่อผู้ใช้นี้ถูกใช้งานแล้ว");
        }

        // ตรวจสอบว่า email ซ้ำหรือไม่ (ถ้ามี method นี้ใน repository)
        // Optional<User> existingEmail = userRepository.findByEmail(email);
        // if (existingEmail.isPresent()) {
        // throw new IllegalArgumentException("อีเมลนี้ถูกใช้งานแล้ว");
        // }

        // สร้างผู้ใช้ใหม่
        User user = new User();
        user.setFullname(fullname.trim());
        user.setUsername(username.trim().toLowerCase()); // แปลงเป็นตัวพิมพ์เล็ก
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(encoder.encode(password));
        user.setRole(role != null && !role.isEmpty() ? role : "USER"); // default เป็น USER
        user.setActive(true);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastLogin(null);

        return userRepository.save(user);
    }

    /**
     * ล็อกอินผู้ใช้
     */
    public User login(String username, String password) {
        // ตรวจสอบข้อมูลที่จำเป็น
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอกชื่อผู้ใช้");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("กรุณากรอกรหัสผ่าน");
        }

        // ค้นหาผู้ใช้
        User user = userRepository.findByUsername(username.trim().toLowerCase());

        if (user == null) {
            throw new IllegalArgumentException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
        }

        // ตรวจสอบว่าบัญชีถูกระงับหรือไม่
        if (!user.getActive() || !user.getIsActive()) {
            throw new IllegalArgumentException("บัญชีของคุณถูกระงับ กรุณาติดต่อผู้ดูแลระบบ");
        }

        // ตรวจสอบรหัสผ่าน
        if (!encoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
        }

        // อัพเดทเวลา login ล่าสุด
        user.setLastLogin(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * ค้นหาผู้ใช้จาก username
     */
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username.trim().toLowerCase());
    }

    /**
     * ค้นหาผู้ใช้จาก id
     */
    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    /**
     * ตรวจสอบว่า username มีอยู่แล้วหรือไม่
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username.trim().toLowerCase()) != null;
    }
}