package com.example.arinternship.controller;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import com.example.arinternship.mapper.StudentMapper;
import com.example.arinternship.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final String UPLOAD_DIR = "uploads/";

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
    public ResponseEntity<StudentDTO> createStudent(
            @RequestParam("student") String studentJson,
            @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestParam(value = "projectFile", required = false) MultipartFile projectFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StudentDTO studentDTO = objectMapper.readValue(studentJson, StudentDTO.class);

            // Handle profile file upload
            if (profileFile != null && !profileFile.isEmpty()) {
                String profileFileName = saveFile(profileFile, "profile");
                studentDTO.setProfileFile(profileFileName);
            }

            // Handle project file upload
            if (projectFile != null && !projectFile.isEmpty()) {
                String projectFileName = saveFile(projectFile, "project");
                studentDTO.setProjectFile(projectFileName);
            }

            Student student = studentMapper.toEntity(studentDTO);
            Student savedStudent = studentService.save(student);
            StudentDTO responseDTO = studentMapper.toDTO(savedStudent);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @RequestParam("student") String studentJson,
            @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestParam(value = "projectFile", required = false) MultipartFile projectFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StudentDTO studentDTO = objectMapper.readValue(studentJson, StudentDTO.class);

            // Get existing student to preserve file names if not uploading new ones
            Student existingStudent = studentService.findById(id);

            // Handle profile file upload
            if (profileFile != null && !profileFile.isEmpty()) {
                String profileFileName = saveFile(profileFile, "profile");
                studentDTO.setProfileFile(profileFileName);
            } else {
                studentDTO.setProfileFile(existingStudent.getProfileFile());
            }

            // Handle project file upload
            if (projectFile != null && !projectFile.isEmpty()) {
                String projectFileName = saveFile(projectFile, "project");
                studentDTO.setProjectFile(projectFileName);
            } else {
                studentDTO.setProjectFile(existingStudent.getProjectFile());
            }

            Student student = studentMapper.toEntity(studentDTO);
            Student updatedStudent = studentService.update(id, student);
            StudentDTO responseDTO = studentMapper.toDTO(updatedStudent);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/grade")
    public ResponseEntity<StudentDTO> updateGrade(
            @PathVariable Long id,
            @RequestBody StudentDTO studentDTO) {
        try {
            Student student = studentService.findById(id);
            student.setGrade(studentDTO.getGrade());
            Student updatedStudent = studentService.save(student);
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

    private String saveFile(MultipartFile file, String type) throws IOException {
        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR + type);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return type + "/" + uniqueFilename;
    }
}