package com.example.arinternship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.arinternship.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
