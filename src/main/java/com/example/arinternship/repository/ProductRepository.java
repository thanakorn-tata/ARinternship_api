package com.example.arinternship.repository;

import com.example.arinternship.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // ✅ ดึงเฉพาะสินค้าที่ active สำหรับหน้าร้าน
    List<Product> findByActiveTrue();
}