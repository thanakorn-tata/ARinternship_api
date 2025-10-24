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

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    // ========================================
    // Basic CRUD Operations (Entity-based)
    // ========================================
    
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
        
        // Copy non-null properties
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
        
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ไม่พบข้อมูลนักศึกษา ID: " + id);
        }
        repository.deleteById(id);
    }

    // ========================================
    // Advanced Operations (DTO-based with Files)
    // ========================================
    
    @Override
    public Student createStudent(StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile) {
        Student student = toEntity(dto);
        
        // Handle file uploads
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                student.setProfileFile(saveFile(profileFile, "profile"));
            } catch (IOException e) {
                throw new RuntimeException("เกิดข้อผิดพลาดในการอัปโหลดรูปโปรไฟล์", e);
            }
        }
        
        if (projectFile != null && !projectFile.isEmpty()) {
            try {
                student.setProjectFile(saveFile(projectFile, "project"));
                student.setProjectFileName(projectFile.getOriginalFilename());
                student.setProjectFileType(projectFile.getContentType());
                
                // ถ้าต้องการเก็บเป็น BLOB
                student.setProjectFileData(projectFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("เกิดข้อผิดพลาดในการอัปโหลดไฟล์โปรเจกต์", e);
            }
        }
        
        return repository.save(student);
    }

    @Override
    public Student updateStudent(Long id, StudentDTO dto, MultipartFile profileFile, MultipartFile projectFile) {
        Student existing = findById(id);
        
        // Update basic fields
        existing.setFullname(dto.getFullname());
        existing.setUniversity(dto.getUniversity());
        existing.setFaculty(dto.getFaculty());
        existing.setMajor(dto.getMajor());
        existing.setContactNumber(dto.getContactNumber());
        existing.setEmail(dto.getEmail());
        existing.setInternDepartment(dto.getInternDepartment());
        existing.setInternDuration(dto.getInternDuration());
        existing.setAttachedProject(dto.getAttachedProject());
        
        // Update files if provided
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                existing.setProfileFile(saveFile(profileFile, "profile"));
            } catch (IOException e) {
                throw new RuntimeException("เกิดข้อผิดพลาดในการอัปโหลดรูปโปรไฟล์", e);
            }
        }
        
        if (projectFile != null && !projectFile.isEmpty()) {
            try {
                existing.setProjectFile(saveFile(projectFile, "project"));
                existing.setProjectFileName(projectFile.getOriginalFilename());
                existing.setProjectFileType(projectFile.getContentType());
                existing.setProjectFileData(projectFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("เกิดข้อผิดพลาดในการอัปโหลดไฟล์โปรเจกต์", e);
            }
        }
        
        return repository.save(existing);
    }

    @Override
    public Student updateGrade(Long id, String grade) {
        Student student = findById(id);
        student.setGrade(grade);
        return repository.save(student);
    }

    // ========================================
    // Conversion Methods (DTO <-> Entity)
    // ========================================
    
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

    // ========================================
    // Helper Methods
    // ========================================
    
    private String saveFile(MultipartFile file, String type) throws IOException {
        // TODO: Implement actual file saving logic
        // For now, just return a placeholder path
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String path = "uploads/" + type + "/" + filename;
        
        // Save file to disk (implement this based on your storage strategy)
        // Files.copy(file.getInputStream(), Paths.get(path));
        
        return path;
    }
}