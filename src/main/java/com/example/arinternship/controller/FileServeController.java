package com.example.arinternship.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/uploads")
public class FileServeController {  // ✅ ใช้ชื่อเดิม FileServeController

    @Value("${file.upload-dir:uploads/}")
    private String UPLOAD_DIR;

    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(  // ✅ ใช้ชื่อเดิม serveFile
            @PathVariable String type,
            @PathVariable String filename) {

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(type).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // ✅ กำหนด Content-Type ตามนามสกุลไฟล์
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // ✅ สำคัญมาก: ต้องมี Content-Disposition เพื่อให้ browser download
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}