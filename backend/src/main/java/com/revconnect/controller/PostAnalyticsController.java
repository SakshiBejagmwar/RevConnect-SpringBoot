package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AnalyticsService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/analytics")
public class PostAnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // Get analytics for a post (only post author can view)
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPostAnalytics(@PathVariable Long postId,
                                              Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            
            // Check if post exists and user is the author
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Post post = postOpt.get();
            if (!post.getAuthorId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "You are not authorized to view analytics for this post"));
            }
            
            PostAnalytics analytics = analyticsService.getAnalytics(postId);
            if (analytics == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(analytics);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get engagement rate for a post
    @GetMapping("/post/{postId}/engagement")
    public ResponseEntity<?> getEngagementRate(@PathVariable Long postId,
                                                Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            
            // Check if post exists and user is the author
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Post post = postOpt.get();
            if (!post.getAuthorId().equals(userId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "You are not authorized to view analytics for this post"));
            }
            
            double engagement = analyticsService.calculateEngagement(postId);
            return ResponseEntity.ok(Map.of("engagementRate", engagement));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOpt.get().getId();
    }
}
