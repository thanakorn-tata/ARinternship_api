package com.example.arinternship.service;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    
    // ✅ ใช้ Entity สำหรับ Database operations
    List<Student> findAll();
    Student findById(Long id);
    Student save(Student student);
    Student update(Long id, Student student);
    void delete(Long id);
    
    // ✅ ใช้ DTO สำหรับ API operations
    Student createStudent(StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile);
    Student updateStudent(Long id, StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile);
    Student updateGrade(Long id, String grade);
    
    // ✅ Conversion methods
    StudentDTO toDTO(Student student);
    Student toEntity(StudentDTO dto);
}