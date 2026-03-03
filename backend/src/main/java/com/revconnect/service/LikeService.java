package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revconnect.repository.LikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.entity.Like;
import com.revconnect.entity.Post;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private AnalyticsService analyticsService;

    // ✅ LIKE POST
    public Like likePost(Long userId, Long postId) {
        // Check if already liked
        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);
        if (existingLike.isPresent()) {
            throw new RuntimeException("You have already liked this post");
        }

        // Get post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Create like
        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        
        Like savedLike = likeRepository.save(like);

        // Increment analytics
        analyticsService.incrementLikes(postId);

        // Create notification (only if not liking own post)
        if (!post.getAuthorId().equals(userId)) {
            notificationService.createNotification(
                    userId,
                    post.getAuthorId(),
                    "LIKE",
                    post.getId(),
                    "Someone liked your post"
            );
        }

        return savedLike;
    }

    // ✅ UNLIKE POST
    public void unlikePost(Long userId, Long postId) {
        Like existingLike = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new RuntimeException("You haven't liked this post"));

        likeRepository.delete(existingLike);

        // Decrement analytics
        analyticsService.decrementLikes(postId);
    }

    // ✅ CHECK IF USER LIKED POST
    public boolean hasUserLikedPost(Long userId, Long postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    // ✅ COUNT LIKES
    public Long countLikes(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    // ✅ GET ALL LIKES FOR A POST
    public List<Like> getLikesByPost(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    // ✅ GET USER'S LIKED POSTS
    public List<Like> getUserLikes(Long userId) {
        return likeRepository.findByUserId(userId);
    }
}
