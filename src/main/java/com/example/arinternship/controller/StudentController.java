package com.example.arinternship.controller;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import com.example.arinternship.mapper.StudentMapper;
import com.example.arinternship.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<Student> students = studentService.findAll();
        List<StudentDTO> studentDTOs = students.stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentService.findById(id);
            StudentDTO dto = studentMapper.toDTO(student);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        Student student = studentMapper.toEntity(studentDTO);
        Student savedStudent = studentService.save(student);
        StudentDTO responseDTO = studentMapper.toDTO(savedStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentDTO studentDTO) {
        try {
            Student student = studentMapper.toEntity(studentDTO);
            Student updatedStudent = studentService.update(id, student);
            StudentDTO responseDTO = studentMapper.toDTO(updatedStudent);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}