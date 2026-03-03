package com.revconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from these origins
        configuration.setAllowedOrigins(java.util.Arrays.asList(
            "http://localhost:4200",   // Angular frontend
            "http://localhost:3000",   // Alternative dev port
            "http://localhost:8080"    // Backend itself (if needed)
        ));
        
        // Allow these HTTP methods
        configuration.setAllowedMethods(java.util.Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Allow these request headers
        configuration.setAllowedHeaders(java.util.Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept"
        ));
        
        // Expose these response headers to frontend
        configuration.setExposedHeaders(java.util.Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size"
        ));
        
        // Cache CORS configuration for 1 hour
        configuration.setMaxAge(3600L);
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Register CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}