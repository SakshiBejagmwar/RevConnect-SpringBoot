package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revconnect.entity.Post;
import com.revconnect.entity.ScheduledPost;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ScheduledPostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledPostService {

    @Autowired
    private ScheduledPostRepository scheduledPostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private AnalyticsService analyticsService;

    // Create scheduled post
    public ScheduledPost schedulePost(ScheduledPost scheduledPost) {
        return scheduledPostRepository.save(scheduledPost);
    }

    // Get all scheduled posts for a user
    public List<ScheduledPost> getUserScheduledPosts(Long userId) {
        return scheduledPostRepository.findByUserIdAndPostedFalse(userId);
    }

    // Delete scheduled post
    public void deleteScheduledPost(Long id) {
        scheduledPostRepository.deleteById(id);
    }

    // Process scheduled posts (runs every minute)
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void processScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledPost> duePosts = scheduledPostRepository
            .findByScheduledTimeBeforeAndPostedFalse(now);

        for (ScheduledPost scheduledPost : duePosts) {
            try {
                // Create actual post
                Post post = new Post();
                post.setContent(scheduledPost.getContent());
                post.setAuthorId(scheduledPost.getUserId());
                post.setCreatedAt(LocalDateTime.now());
                
                Post savedPost = postRepository.save(post);

                // Process hashtags
                hashtagService.processHashtags(post.getContent(), savedPost);

                // Initialize analytics
                analyticsService.initializeAnalytics(savedPost.getId());

                // Mark as posted
                scheduledPost.setPosted(true);
                scheduledPostRepository.save(scheduledPost);

                System.out.println("Posted scheduled post ID: " + scheduledPost.getId());
            } catch (Exception e) {
                System.err.println("Error posting scheduled post: " + e.getMessage());
            }
        }
    }

    // Get all scheduled posts (for admin)
    public List<ScheduledPost> getAllScheduledPosts() {
        return scheduledPostRepository.findAll();
    }
}
