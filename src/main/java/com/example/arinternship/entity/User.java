package com.example.arinternship.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User implements Serializable {   // ✅ เพิ่ม Serializable สำหรับ Session

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String fullname;

    @Column(nullable = false, length = 20)
    private String role;

    @Column
    private Boolean active = true;

    // ✅ ที่อยู่จัดส่ง default
    @Column(name = "default_name", length = 255)
    private String defaultName;

    @Column(name = "default_phone", length = 20)
    private String defaultPhone;

    @Column(name = "default_address", columnDefinition = "TEXT")
    private String defaultAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ Set updatedAt อัตโนมัติทุกครั้งที่ update
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =====================
    // Getter / Setter
    // =====================

    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getFullname()              { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getRole()                  { return role; }
    public void setRole(String role)         { this.role = role; }

    public Boolean getActive()               { return active; }
    public void setActive(Boolean active)    { this.active = active; }

    public String getDefaultName()                   { return defaultName; }
    public void setDefaultName(String defaultName)   { this.defaultName = defaultName; }

    public String getDefaultPhone()                  { return defaultPhone; }
    public void setDefaultPhone(String defaultPhone) { this.defaultPhone = defaultPhone; }

    public String getDefaultAddress()                    { return defaultAddress; }
    public void setDefaultAddress(String defaultAddress) { this.defaultAddress = defaultAddress; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)      { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)      { this.updatedAt = updatedAt; }
}