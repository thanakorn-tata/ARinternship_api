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

import jakarta.servlet.http.HttpSession;
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

    /**
     * ✅ API ตรวจสอบสิทธิ์การแก้ไข
     * GET /api/student/{id}/can-edit
     */
    @GetMapping("/{id}/can-edit")
    public ResponseEntity<Map<String, Object>> canEditStudent(
            @PathVariable Long id,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // ✅ ดึงข้อมูลจาก Session
            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            
            System.out.println("🔍 Session Debug:");
            System.out.println("   - role: " + role);
            System.out.println("   - userId: " + userId);
            System.out.println("   - studentId: " + id);
            
            // ✅ ตรวจสอบว่า Login หรือยัง
            if (role == null || userId == null) {
                response.put("canEdit", false);
                response.put("reason", "ไม่ได้เข้าสู่ระบบ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // ✅ ADMIN แก้ไขได้ทั้งหมด
            if ("ADMIN".equals(role)) {
                response.put("canEdit", true);
                response.put("reason", "คุณเป็นผู้ดูแลระบบ");
                System.out.println("✅ ADMIN - Full Access");
                return ResponseEntity.ok(response);
            }
            
            // ✅ USER แก้ไขได้เฉพาะที่ตัวเองสร้าง
            Student student = studentService.findById(id);
            boolean canEdit = student.getCreatedBy() != null && 
                            student.getCreatedBy().equals(userId);
            
            response.put("canEdit", canEdit);
            if (!canEdit) {
                response.put("reason", "คุณไม่ใช่ผู้สร้างข้อมูลนี้");
            } else {
                response.put("reason", "คุณเป็นผู้สร้างข้อมูลนี้");
            }
            
            System.out.println("✅ Permission Check Result: canEdit=" + canEdit);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Error in canEditStudent: " + e.getMessage());
            response.put("canEdit", false);
            response.put("reason", "ไม่พบข้อมูลนักศึกษา");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * ✅ สร้างนักศึกษาพร้อมไฟล์
     * POST /api/student/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> createStudentWithFiles(
            @RequestPart("student") String studentJson,
            @RequestPart(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile,
            HttpSession session) {

        try {
            // ✅ ดึง userId จาก Session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "กรุณาเข้าสู่ระบบก่อน");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            ObjectMapper mapper = new ObjectMapper();
            StudentDTO dto = mapper.readValue(studentJson, StudentDTO.class);

            // ✅ กำหนด created_by
            dto.setCreatedBy(userId);

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

    /**
     * ✅ อัปเดตนักศึกษาพร้อมไฟล์
     * PUT /api/student/upload/{id}
     */
    @PutMapping("/upload/{id}")
    public ResponseEntity<?> updateStudentWithFiles(
            @PathVariable Long id,
            @RequestPart("student") String studentJson,
            @RequestPart(value = "profileFile", required = false) MultipartFile profileFile,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile,
            HttpSession session) {

        try {
            // ✅ ตรวจสอบสิทธิ์
            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "กรุณาเข้าสู่ระบบก่อน");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            Student existing = studentService.findById(id);
            
            // ✅ ถ้าไม่ใช่ ADMIN และไม่ใช่เจ้าของข้อมูล = ห้ามแก้ไข
            if (!"ADMIN".equals(role)) {
                if (existing.getCreatedBy() == null || 
                    !existing.getCreatedBy().equals(userId)) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "คุณไม่มีสิทธิ์แก้ไขข้อมูลนี้");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            StudentDTO dto = mapper.readValue(studentJson, StudentDTO.class);

            // ✅ คัดลอกข้อมูล (ไม่เปลี่ยน id, createdAt, createdBy)
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

    /**
     * ✅ อัปเดตนักศึกษา (JSON only)
     * PUT /api/student/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudentJson(
            @PathVariable Long id,
            @RequestBody StudentDTO dto,
            HttpSession session) {

        try {
            // ✅ ตรวจสอบสิทธิ์
            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "กรุณาเข้าสู่ระบบก่อน");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            Student existing = studentService.findById(id);
            
            // ✅ ถ้าไม่ใช่ ADMIN และไม่ใช่เจ้าของข้อมูล = ห้ามแก้ไข
            if (!"ADMIN".equals(role)) {
                if (existing.getCreatedBy() == null || 
                    !existing.getCreatedBy().equals(userId)) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "คุณไม่มีสิทธิ์แก้ไขข้อมูลนี้");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }
            }

            // ✅ อัปเดตเฉพาะฟิลด์ที่ส่งมา
            if (dto.getFullname() != null) existing.setFullname(dto.getFullname());
            if (dto.getUniversity() != null) existing.setUniversity(dto.getUniversity());
            if (dto.getFaculty() != null) existing.setFaculty(dto.getFaculty());
            if (dto.getMajor() != null) existing.setMajor(dto.getMajor());
            if (dto.getContactNumber() != null) existing.setContactNumber(dto.getContactNumber());
            if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
            if (dto.getInternDepartment() != null) existing.setInternDepartment(dto.getInternDepartment());
            if (dto.getInternDuration() != null) existing.setInternDuration(dto.getInternDuration());
            if (dto.getAttachedProject() != null) existing.setAttachedProject(dto.getAttachedProject());
            if (dto.getGrade() != null) existing.setGrade(dto.getGrade());

            Student updated = studentService.save(existing);

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

    /**
     * ✅ ลบนักศึกษา
     * DELETE /api/student/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(
            @PathVariable Long id,
            HttpSession session) {
        try {
            // ✅ ตรวจสอบสิทธิ์
            String role = (String) session.getAttribute("role");
            Long userId = (Long) session.getAttribute("userId");
            
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "กรุณาเข้าสู่ระบบก่อน");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            Student existing = studentService.findById(id);
            
            // ✅ ถ้าไม่ใช่ ADMIN และไม่ใช่เจ้าของข้อมูล = ห้ามลบ
            if (!"ADMIN".equals(role)) {
                if (existing.getCreatedBy() == null || 
                    !existing.getCreatedBy().equals(userId)) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "คุณไม่มีสิทธิ์ลบข้อมูลนี้");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }
            }
            
            studentService.delete(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ลบข้อมูลสำเร็จ");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ✅ บันทึกไฟล์
     */
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