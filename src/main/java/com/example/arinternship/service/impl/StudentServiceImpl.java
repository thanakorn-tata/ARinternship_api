package com.example.arinternship.service.impl;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import com.example.arinternship.repository.StudentRepository;
import com.example.arinternship.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลนักศึกษา ID: " + id));
    }

    @Override
    public Student save(Student student) {
        return repository.save(student);
    }

    @Override
    public Student update(Long id, Student student) {
        Student existing = findById(id);
        BeanUtils.copyProperties(student, existing, "id", "createdAt", "createdBy");
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ไม่พบข้อมูลนักศึกษา ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Student createStudent(StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile) {
        Student student = toEntity(dto);
        return repository.save(student);
    }

    @Override
    public Student updateStudent(Long id, StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile) {
        Student student = findById(id);
        BeanUtils.copyProperties(dto, student, "id", "createdAt", "createdBy");
        return repository.save(student);
    }

    @Override
    public Student updateGrade(Long id, String grade) {
        Student student = findById(id);
        student.setGrade(grade);
        return repository.save(student);
    }

    @Override
    public StudentDTO toDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        BeanUtils.copyProperties(student, dto);
        return dto;
    }

    @Override
    public Student toEntity(StudentDTO dto) {
        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        return student;
    }
}
