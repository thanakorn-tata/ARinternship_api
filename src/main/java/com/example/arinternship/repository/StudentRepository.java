package com.example.arinternship.repository;

import com.example.arinternship.entity.Student; // ✅ ต้องเป็น Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> { // ✅ Student ไม่ใช่ StudentDTO
    
    List<Student> findByCreatedBy(Long createdBy);
    List<Student> findByUniversityContainingIgnoreCase(String university);
    List<Student> findByGrade(String grade);
}