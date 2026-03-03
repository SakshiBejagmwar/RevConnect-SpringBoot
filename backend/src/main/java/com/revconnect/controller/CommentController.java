package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.CommentService;
import com.revconnect.entity.Comment;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    // Add comment to a post
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> commentData,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Long postId = Long.valueOf(commentData.get("postId").toString());
            String content = commentData.get("content").toString();
            
            Comment comment = commentService.addComment(userId, postId, content);
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }
    
    // Get comment count for a post
    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<String, Long>> getCommentCount(@PathVariable Long postId) {
        Long count = commentService.getCommentCount(postId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Get current user's comments
    @GetMapping("/my-comments")
    public ResponseEntity<List<Comment>> getMyComments(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Comment> comments = commentService.getUserComments(userId);
        return ResponseEntity.ok(comments);
    }

    // Delete comment (with authorization)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            commentService.deleteComment(id, userId);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
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
