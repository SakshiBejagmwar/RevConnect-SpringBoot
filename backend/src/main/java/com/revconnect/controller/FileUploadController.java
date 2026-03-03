package com.revconnect.controller;

import com.revconnect.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Please select a file to upload"));
            }

            // Validate file size (max 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(Map.of("message", "File size exceeds maximum limit of 10MB"));
            }

            // Validate file type (images and common documents)
            String contentType = file.getContentType();
            if (contentType == null || !isValidFileType(contentType)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid file type. Allowed: images, PDF, DOC, DOCX, TXT"));
            }

            // Store file
            String filePath = fileStorageService.storeFile(file);

            // Return file info
            Map<String, Object> response = new HashMap<>();
            response.put("fileName", file.getOriginalFilename());
            response.put("filePath", filePath);
            response.put("fileType", contentType);
            response.put("fileSize", file.getSize());
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            // Remove leading slash if present
            String fileName = filePath.replace("/uploads/", "");
            Path fileStorageLocation = fileStorageService.getFileStorageLocation();
            Path file = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            boolean deleted = fileStorageService.deleteFile(filePath);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "File not found or could not be deleted"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Failed to delete file: " + e.getMessage()));
        }
    }

    private boolean isValidFileType(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               contentType.equals("text/plain") ||
               contentType.equals("video/mp4") ||
               contentType.equals("video/mpeg") ||
               contentType.equals("video/quicktime");
    }
}
