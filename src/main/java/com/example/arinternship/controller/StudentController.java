package com.example.arinternship.controller;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import com.example.arinternship.mapper.StudentMapper;
import com.example.arinternship.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @Value("${file.upload-dir:uploads/}")
    private String UPLOAD_DIR;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.findAll()
                .stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentService.findById(id);
            return ResponseEntity.ok(studentMapper.toDTO(student));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createStudentWithFiles(
            @RequestPart("student") String studentJson,
            @RequestPart(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            StudentDTO dto = mapper.readValue(studentJson, StudentDTO.class);

            if (profileFile != null && !profileFile.isEmpty()) {
                dto.setProfileFile(saveFile(profileFile, "profile"));
            }
            if (projectFile != null && !projectFile.isEmpty()) {
                dto.setProjectFile(saveFile(projectFile, "project"));
            }

            Student saved = studentService.save(studentMapper.toEntity(dto));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "เพิ่มข้อมูลนักศึกษาสำเร็จ");
            response.put("data", studentMapper.toDTO(saved));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "เกิดข้อผิดพลาดในการบันทึกข้อมูล: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/upload/{id}")
    public ResponseEntity<?> updateStudentWithFiles(
            @PathVariable Long id,
            @RequestPart("student") String studentJson,
            @RequestPart(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            StudentDTO dto = mapper.readValue(studentJson, StudentDTO.class);
            Student existing = studentService.findById(id);

            BeanUtils.copyProperties(dto, existing, "id", "createdAt", "createdBy");

            if (profileFile != null && !profileFile.isEmpty()) {
                existing.setProfileFile(saveFile(profileFile, "profile"));
            }
            if (projectFile != null && !projectFile.isEmpty()) {
                existing.setProjectFile(saveFile(projectFile, "project"));
            }

            Student updated = studentService.save(existing);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "แก้ไขข้อมูลนักศึกษาสำเร็จ");
            response.put("data", studentMapper.toDTO(updated));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "เกิดข้อผิดพลาดในการอัปเดตข้อมูล: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ✅ เพิ่ม endpoint ใหม่สำหรับ update JSON (สำหรับอัพเดทเกรด)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudentJson(
            @PathVariable Long id,
            @RequestBody StudentDTO dto) {

        try {
            Student existing = studentService.findById(id);

            // อัพเดทเฉพาะ field ที่มีค่า
            if (dto.getFullname() != null) existing.setFullname(dto.getFullname());
            if (dto.getUniversity() != null) existing.setUniversity(dto.getUniversity());
            if (dto.getFaculty() != null) existing.setFaculty(dto.getFaculty());
            if (dto.getMajor() != null) existing.setMajor(dto.getMajor());
            if (dto.getContactNumber() != null) existing.setContactNumber(dto.getContactNumber());
            if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
            if (dto.getInternDepartment() != null) existing.setInternDepartment(dto.getInternDepartment());
            if (dto.getInternDuration() != null) existing.setInternDuration(dto.getInternDuration());
            if (dto.getAttachedProject() != null) existing.setAttachedProject(dto.getAttachedProject());
            
            // ✅ สำคัญมาก - อัพเดทเกรด
            if (dto.getGrade() != null) {
                existing.setGrade(dto.getGrade());
                System.out.println("✅ Updating grade to: " + dto.getGrade());
            }

            Student updated = studentService.save(existing);
            System.out.println("✅ Grade saved to database: " + updated.getGrade());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "แก้ไขข้อมูลสำเร็จ");
            response.put("data", studentMapper.toDTO(updated));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "ไม่พบข้อมูลนักศึกษา: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "เกิดข้อผิดพลาด: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
        Path dir = Paths.get(UPLOAD_DIR + type);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse("");
        String newName = UUID.randomUUID() + ext;

        Path filePath = dir.resolve(newName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return type + "/" + newName;
    }
}