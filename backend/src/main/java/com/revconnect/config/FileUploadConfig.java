package com.revconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from /uploads directory
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        String uploadPath = uploadDir.toUri().toString();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
