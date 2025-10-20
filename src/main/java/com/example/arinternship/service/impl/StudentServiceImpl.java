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
                .orElseThrow(() -> new RuntimeException("Student not found"));
        existing.setFullName(student.getFullName());
        existing.setUniversity(student.getUniversity());
        existing.setFaculty(student.getFaculty());
        existing.setMajor(student.getMajor());
        existing.setPhone(student.getPhone());
        existing.setEmail(student.getEmail());
        existing.setDepartment(student.getDepartment());
        existing.setInternshipPeriod(student.getInternshipPeriod());
        existing.setComment(student.getComment());
        existing.setProfileFile(student.getProfileFile());
        existing.setProjectFile(student.getProjectFile());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
