package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.PostService;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create post (authenticated users only)
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            
            // Set author from authentication
            post.setAuthorId(userId);
            
            Post created = postService.createPost(post);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get all posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // Get posts by author
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable Long authorId) {
        List<Post> posts = postService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    // Get my posts
    @GetMapping("/my-posts")
    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Post> posts = postService.getPostsByAuthor(userId);
        return ResponseEntity.ok(posts);
    }

    // Update post (with authorization)
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, 
                                        @RequestBody Post post,
                                        Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Post updated = postService.updatePost(id, post, userId);
            
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    // Delete post (with authorization)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            postService.deletePost(id, userId);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }
    
    // Get personalized feed (authenticated user)
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Post> feed = postService.getSmartFeed(userId);
        return ResponseEntity.ok(feed);
    }
    
    // Share/repost (authenticated user)
    @PostMapping("/share/{postId}")
    public ResponseEntity<?> sharePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Post shared = postService.sharePost(userId, postId);
            return ResponseEntity.ok(shared);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Pin post (for creators/businesses)
    @PutMapping("/pin/{postId}")
    public ResponseEntity<?> pinPost(@PathVariable Long postId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Post pinned = postService.pinPost(postId, userId);
            
            if (pinned == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(pinned);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    // Unpin post
    @PutMapping("/unpin/{postId}")
    public ResponseEntity<?> unpinPost(@PathVariable Long postId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Post unpinned = postService.unpinPost(postId, userId);
            
            if (unpinned == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(unpinned);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    // Get trending posts
    @GetMapping("/trending")
    public ResponseEntity<List<Post>> getTrendingPosts() {
        List<Post> trending = postService.getTrendingPosts();
        return ResponseEntity.ok(trending);
    }

    // Filter feed by post type
    @GetMapping("/feed/filter/type")
    public ResponseEntity<List<Post>> getFeedByPostType(@RequestParam String postType,
                                                         Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Post> posts = postService.getFeedByPostType(userId, postType);
        return ResponseEntity.ok(posts);
    }

    // Filter posts by user type (PERSONAL, CREATOR, BUSINESS)
    @GetMapping("/feed/filter/user-type")
    public ResponseEntity<List<Post>> getFeedByUserType(@RequestParam String userType,
                                                         Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Post> posts = postService.getFeedByUserType(userId, userType);
        return ResponseEntity.ok(posts);
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
