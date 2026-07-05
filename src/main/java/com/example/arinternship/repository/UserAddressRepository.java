package com.example.arinternship.repository;

import com.example.arinternship.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserIdOrderByIsDefaultDescCreatedAtAsc(Long userId);

    Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.userId = :userId")
    void clearDefaultByUserId(Long userId);
}