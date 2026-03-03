package com.revconnect.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        // Create uploads directory in the project root
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Get original filename
            String originalFilename = file.getOriginalFilename();
            
            // Generate unique filename
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return the file path (relative to uploads directory)
            return "/uploads/" + newFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                return false;
            }

            // Remove leading slash if present
            String fileName = filePath.replace("/uploads/", "");
            Path fileToDelete = this.fileStorageLocation.resolve(fileName).normalize();
            
            return Files.deleteIfExists(fileToDelete);
        } catch (IOException ex) {
            return false;
        }
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
