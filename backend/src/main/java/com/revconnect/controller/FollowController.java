package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.revconnect.service.FollowService;
import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    // Follow a user
    @PostMapping("/{followingId}")
    public ResponseEntity<?> follow(@PathVariable Long followingId,
                                    Authentication authentication) {
        try {
            Long followerId = getUserIdFromAuth(authentication);
            Follow follow = followService.followUser(followerId, followingId);
            return ResponseEntity.ok(follow);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Unfollow a user
    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollow(@PathVariable Long followingId,
                                      Authentication authentication) {
        try {
            Long followerId = getUserIdFromAuth(authentication);
            followService.unfollowUser(followerId, followingId);
            return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    // Accept follow request
    @PutMapping("/accept/{followId}")
    public ResponseEntity<?> acceptFollowRequest(@PathVariable Long followId,
                                                  Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Follow follow = followService.acceptFollowRequest(followId, userId);
            return ResponseEntity.ok(follow);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    // Reject follow request
    @DeleteMapping("/reject/{followId}")
    public ResponseEntity<?> rejectFollowRequest(@PathVariable Long followId,
                                                  Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            followService.rejectFollowRequest(followId, userId);
            return ResponseEntity.ok(Map.of("message", "Follow request rejected"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    // Get pending follow requests (received)
    @GetMapping("/requests")
    public ResponseEntity<List<Follow>> getPendingRequests(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Follow> requests = followService.getPendingFollowRequests(userId);
        return ResponseEntity.ok(requests);
    }

    // Get my following list
    @GetMapping("/following")
    public ResponseEntity<List<Follow>> getMyFollowing(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Follow> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    // Get my followers list
    @GetMapping("/followers")
    public ResponseEntity<List<Follow>> getMyFollowers(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Follow> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    // Get following list for a specific user
    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Follow>> getUserFollowing(@PathVariable Long userId) {
        List<Follow> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    // Get followers list for a specific user
    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Follow>> getUserFollowers(@PathVariable Long userId) {
        List<Follow> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    // Count my followers
    @GetMapping("/count/followers")
    public ResponseEntity<Map<String, Long>> countMyFollowers(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Long count = followService.countFollowers(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Count my following
    @GetMapping("/count/following")
    public ResponseEntity<Map<String, Long>> countMyFollowing(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Long count = followService.countFollowing(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Count followers for a specific user
    @GetMapping("/count/followers/{userId}")
    public ResponseEntity<Map<String, Long>> countUserFollowers(@PathVariable Long userId) {
        Long count = followService.countFollowers(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Count following for a specific user
    @GetMapping("/count/following/{userId}")
    public ResponseEntity<Map<String, Long>> countUserFollowing(@PathVariable Long userId) {
        Long count = followService.countFollowing(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    // Check if I'm following a user
    @GetMapping("/status/{followingId}")
    public ResponseEntity<Map<String, Boolean>> checkFollowStatus(@PathVariable Long followingId,
                                                                   Authentication authentication) {
        Long followerId = getUserIdFromAuth(authentication);
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
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
