package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revconnect.entity.ScheduledPost;
import com.revconnect.service.ScheduledPostService;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-posts")
public class ScheduledPostController {

    @Autowired
    private ScheduledPostService scheduledPostService;

    // Create scheduled post
    @PostMapping
    public ResponseEntity<ScheduledPost> schedulePost(@RequestBody ScheduledPost scheduledPost) {
        ScheduledPost created = scheduledPostService.schedulePost(scheduledPost);
        return ResponseEntity.ok(created);
    }

    // Get user's scheduled posts
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScheduledPost>> getUserScheduledPosts(@PathVariable Long userId) {
        List<ScheduledPost> posts = scheduledPostService.getUserScheduledPosts(userId);
        return ResponseEntity.ok(posts);
    }

    // Delete scheduled post
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScheduledPost(@PathVariable Long id) {
        scheduledPostService.deleteScheduledPost(id);
        return ResponseEntity.ok("Scheduled post deleted");
    }

    // Get all scheduled posts (admin)
    @GetMapping
    public ResponseEntity<List<ScheduledPost>> getAllScheduledPosts() {
        List<ScheduledPost> posts = scheduledPostService.getAllScheduledPosts();
        return ResponseEntity.ok(posts);
    }
}
