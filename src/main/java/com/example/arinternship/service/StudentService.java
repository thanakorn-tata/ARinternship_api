package com.example.arinternship.service;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    List<Student> findAll();
    Student findById(Long id);
    Student save(Student student);
    Student update(Long id, Student student);
    void delete(Long id);

    Student createStudent(StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile);
    Student updateStudent(Long id, StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile);
    Student updateGrade(Long id, String grade);

    StudentDTO toDTO(Student student);
    Student toEntity(StudentDTO dto);
}
