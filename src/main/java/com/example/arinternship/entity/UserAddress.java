package com.example.arinternship.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String label;  // เช่น "บ้าน", "ที่ทำงาน"

    @Column(name = "recipient_name", nullable = false, length = 255)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters / Setters
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public Long getUserId()                    { return userId; }
    public void setUserId(Long userId)         { this.userId = userId; }

    public String getLabel()                   { return label; }
    public void setLabel(String label)         { this.label = label; }

    public String getRecipientName()                       { return recipientName; }
    public void setRecipientName(String recipientName)     { this.recipientName = recipientName; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }

    public String getAddress()                 { return address; }
    public void setAddress(String address)     { this.address = address; }

    public Boolean getIsDefault()              { return isDefault; }
    public void setIsDefault(Boolean isDefault){ this.isDefault = isDefault; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime t)  { this.createdAt = t; }
}