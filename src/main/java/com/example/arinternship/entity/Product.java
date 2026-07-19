package com.example.arinternship.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // ✅ ไม่ใช้ @Lob เพราะบน Postgres จะไปเก็บเป็น Large Object (OID/bigint) แทน bytea ตรงๆ
    // ใช้ @JdbcTypeCode(SqlTypes.VARBINARY) เพื่อบังคับให้ map เป็น bytea column ตรงๆ
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @Column(name = "image_type")
    private String imageType;

    // ✅ Postgres ใช้ BOOLEAN แทน MySQL TINYINT(1)
    @Column(name = "active", columnDefinition = "BOOLEAN NOT NULL DEFAULT true")
    private Boolean active = true;
}