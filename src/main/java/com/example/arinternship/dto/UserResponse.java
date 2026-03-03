package com.example.arinternship.dto;

import com.example.arinternship.entity.User;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullname;
    private String role;
    private Boolean active;

    // ✅ Static factory method - แปลงจาก User entity
    public static UserResponse from(User user) {
        if (user == null) return null;

        UserResponse res = new UserResponse();
        res.id       = user.getId();
        res.username = user.getUsername();
        res.email    = user.getEmail();
        res.fullname = user.getFullname();
        res.role     = user.getRole();
        res.active   = user.getActive();
        return res;
    }

    // =====================
    // Getter only (ไม่มี Setter เพราะ DTO ควร immutable)
    // =====================

    public Long getId()        { return id; }
    public String getUsername(){ return username; }
    public String getEmail()   { return email; }
    public String getFullname(){ return fullname; }
    public String getRole()    { return role; }
    public Boolean getActive() { return active; }
}