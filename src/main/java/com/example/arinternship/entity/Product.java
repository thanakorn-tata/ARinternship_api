package com.example.arinternship.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;
    private Integer stock;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_type")
    private String imageType;

    // ✅ ใช้ columnDefinition ระบุชัดเจนให้ตรงกับ MySQL TINYINT(1)
    @Column(name = "active", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 1")
    private Boolean active = true;
}