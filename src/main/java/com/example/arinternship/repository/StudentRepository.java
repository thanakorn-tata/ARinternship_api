package com.example.arinternship.repository;

import com.example.arinternship.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // JpaRepository จะสร้าง method พื้นฐานให้อัตโนมัติ:
    // - findAll()
    // - findById(Long id)
    // - save(Student student)
    // - deleteById(Long id)
    // - existsById(Long id)

    // ถ้าต้องการ custom query เพิ่มตรงนี้
    // เช่น: List<Student> findByUniversity(String university);
}