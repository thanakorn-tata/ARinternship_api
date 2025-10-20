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
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    private final StudentService service;
    private final StudentMapper mapper;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAll() {
        List<Student> students = service.findAll();
        List<StudentDTO> dtos = students.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        List<Student> students = service.findAll();
        Student student = students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(mapper.toDTO(student));
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        Student student = mapper.toEntity(dto);
        Student saved = service.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        Student student = mapper.toEntity(dto);
        Student updated = service.update(id, student);
        return ResponseEntity.ok(mapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}