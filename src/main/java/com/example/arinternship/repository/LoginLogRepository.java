// LoginLogRepository.java
package com.example.arinternship.repository;

import com.example.arinternship.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findByUserIdOrderByTimestampDesc(Long userId);
}