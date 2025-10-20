package com.example.arinternship.service.impl;

import com.example.arinternship.entity.Student;
import com.example.arinternship.repository.StudentRepository;
import com.example.arinternship.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public Student save(Student student) {
        return repository.save(student);
    }

    @Override
    public Student update(Long id, Student student) {
        Student existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        // Update all fields
        existing.setFullname(student.getFullname());
        existing.setUniversity(student.getUniversity());
        existing.setFaculty(student.getFaculty());
        existing.setMajor(student.getMajor());
        existing.setContactNumber(student.getContactNumber());
        existing.setEmail(student.getEmail());
        existing.setInternDepartment(student.getInternDepartment());
        existing.setInternDuration(student.getInternDuration());
        existing.setAttachedProject(student.getAttachedProject());
        existing.setGrade(student.getGrade());
        existing.setCreatedBy(student.getCreatedBy());
        existing.setProfileFile(student.getProfileFile());
        existing.setProjectFile(student.getProjectFile());
        
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        repository.deleteById(id);
    }
}