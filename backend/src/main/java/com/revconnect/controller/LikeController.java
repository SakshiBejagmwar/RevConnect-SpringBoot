package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.LikeService;
import com.revconnect.entity.Like;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserRepository userRepository;

    // Like a post
    @PostMapping("/{postId}")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Like like = likeService.likePost(userId, postId);
            return ResponseEntity.ok(like);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Unlike a post
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            likeService.unlikePost(userId, postId);
            return ResponseEntity.ok(Map.of("message", "Post unliked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Check if current user liked a post
    @GetMapping("/check/{postId}")
    public ResponseEntity<Map<String, Boolean>> checkIfLiked(@PathVariable Long postId, 
                                                              Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        boolean liked = likeService.hasUserLikedPost(userId, postId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    // Get like count for a post
    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<String, Long>> countLikes(@PathVariable Long postId) {
        Long count = likeService.countLikes(postId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Get all users who liked a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPost(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPost(postId);
        return ResponseEntity.ok(likes);
    }

    // Get current user's liked posts
    @GetMapping("/my-likes")
    public ResponseEntity<List<Like>> getMyLikes(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Like> likes = likeService.getUserLikes(userId);
        return ResponseEntity.ok(likes);
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
