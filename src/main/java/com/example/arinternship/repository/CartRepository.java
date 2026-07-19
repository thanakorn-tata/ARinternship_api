package com.example.arinternship.repository;

import com.example.arinternship.entity.Cart;
import com.example.arinternship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // ✅ ดึงตะกร้าทั้งหมดของ user
    List<Cart> findByUser(User user);

    // ✅ หาสินค้าใน cart ของ user (เช็คก่อนเพิ่มซ้ำ)
    Optional<Cart> findByUserAndProductId(User user, Long productId);

    // ✅ ลบทั้งหมดของ user (เมื่อ checkout)
    // ต้องมี @Transactional เพราะ derived delete query ของ Spring Data JPA
    // ต้องมี transaction ครอบไว้เสมอ ไม่งั้นจะโดน TransactionRequiredException
    @Transactional
    void deleteByUser(User user);
}