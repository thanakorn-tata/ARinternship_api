package com.example.arinternship.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.file.*;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:4200")
public class FileServeController {

    @Value("${file.upload-dir:uploads/}")
    private String UPLOAD_DIR;

    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String type,
            @PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + type).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
