package com.example.arinternship.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username ห้ามว่าง")
    @Size(min = 3, max = 50, message = "username ต้องมี 3-50 ตัวอักษร")
    private String username;

    @NotBlank(message = "password ห้ามว่าง")
    @Size(min = 8, message = "password ต้องมีอย่างน้อย 8 ตัวอักษร")
    private String password;

    @Email(message = "รูปแบบ email ไม่ถูกต้อง")
    private String email;

    @NotBlank(message = "ชื่อ-นามสกุลห้ามว่าง")
    private String fullname;

    // =====================
    // Getter / Setter
    // =====================

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getFullname()              { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
}