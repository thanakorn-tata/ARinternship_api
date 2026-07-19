package com.example.arinternship.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @Column(nullable = false)
    private Double totalAmount;

    // PENDING_PAYMENT, SLIP_UPLOADED, CONFIRMED, CANCELLED
    @Column(nullable = false)
    private String status = "PENDING_PAYMENT";

    // ✅ ไม่ใช้ @Lob เพราะบน Postgres จะไปเก็บเป็น Large Object (OID/bigint) แทน bytea ตรงๆ
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "slip_image", columnDefinition = "BYTEA")
    private byte[] slipImage;

    @Column(name = "slip_image_type")
    private String slipImageType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // ข้อมูลผู้รับ
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
}