package com.example.arinternship.service;

import com.example.arinternship.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    Student findById(Long id);
    Student save(Student student);
    Student update(Long id, Student student);
    void delete(Long id);
}