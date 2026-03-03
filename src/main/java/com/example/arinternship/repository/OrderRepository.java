package com.example.arinternship.repository;

import com.example.arinternship.entity.Order;
import com.example.arinternship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    List<Order> findAllByOrderByCreatedAtDesc();
}