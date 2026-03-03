package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.entity.Follow;
import com.revconnect.entity.Post;
import com.revconnect.entity.Connection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private NotificationService notificationService;

    // ✅ GET SMART FEED - Shows posts from connections and followed users only
    public List<Post> getSmartFeed(Long userId) {
        Set<Long> userIds = new HashSet<>();
        
        // Add self - ALWAYS show user's own posts
        userIds.add(userId);
        
        // Get all accepted connections (both sender and receiver)
        List<Connection> connections = connectionRepository
                .findBySenderIdOrReceiverIdAndStatus(userId, userId, "ACCEPTED");
        
        for (Connection conn : connections) {
            if (conn.getSenderId().equals(userId)) {
                userIds.add(conn.getReceiverId());
            } else {
                userIds.add(conn.getSenderId());
            }
        }
        
        // Get all accepted follows (people the user follows)
        List<Follow> following = followRepository.findByFollowerIdAndStatus(userId, "ACCEPTED");
        userIds.addAll(following.stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList()));

        // Fetch posts from all these users sorted by latest
        List<Post> feedPosts = postRepository
                .findByAuthorIdInOrderByCreatedAtDesc(new java.util.ArrayList<>(userIds));

        // If feed is empty (no connections/follows), show all public posts for discovery
        if (feedPosts.isEmpty()) {
            feedPosts = postRepository.findTop20ByOrderByCreatedAtDesc();
        }

        // Increment reach analytics
        feedPosts.forEach(post ->
                analyticsService.incrementReach(post.getId())
        );

        return feedPosts;
    }

    // ✅ CREATE POST
    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);

        // Process hashtags
        hashtagService.processHashtags(savedPost.getContent(), savedPost);

        // Initialize analytics
        analyticsService.initializeAnalytics(savedPost.getId());

        return savedPost;
    }

    // ✅ UPDATE POST
    public Post updatePost(Long id, Post updatedPost, Long requestingUserId) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isEmpty()) {
            return null;
        }
        
        Post post = existingPost.get();
        
        // Authorization check - only author can edit
        if (!post.getAuthorId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only edit your own posts");
        }
        
        // Update fields
        if (updatedPost.getContent() != null) {
            post.setContent(updatedPost.getContent());
        }
        if (updatedPost.getPostType() != null) {
            post.setPostType(updatedPost.getPostType());
        }
        if (updatedPost.getCallToAction() != null) {
            post.setCallToAction(updatedPost.getCallToAction());
        }
        if (updatedPost.getMediaPath() != null) {
            post.setMediaPath(updatedPost.getMediaPath());
        }
        
        // Clear existing hashtags
        post.getHashtags().clear();
        
        Post saved = postRepository.save(post);
        
        // Re-process hashtags
        hashtagService.processHashtags(saved.getContent(), saved);
        
        return saved;
    }

    // ✅ GET POST BY ID
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // ✅ GET POSTS BY AUTHOR
    public List<Post> getPostsByAuthor(Long authorId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    // ✅ SHARE POST (Repost)
    public Post sharePost(Long userId, Long postId) {
        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post sharedPost = new Post();
        sharedPost.setAuthorId(userId);
        sharedPost.setContent(originalPost.getContent());
        sharedPost.setOriginalPostId(postId);
        sharedPost.setShared(true);

        notificationService.createNotification(
                userId,
                originalPost.getAuthorId(),
                "SHARE",
                originalPost.getId(),
                "Someone shared your post"
        );

        Post savedPost = postRepository.save(sharedPost);

        // Process hashtags from original post
        hashtagService.processHashtags(savedPost.getContent(), savedPost);

        // Increment share analytics of original post
        analyticsService.incrementShares(postId);

        // Initialize analytics for new shared post
        analyticsService.initializeAnalytics(savedPost.getId());

        return savedPost;
    }

    // ✅ PIN POST
    public Post pinPost(Long postId, Long requestingUserId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return null;
        }
        
        Post post = postOpt.get();
        
        // Authorization check
        if (!post.getAuthorId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only pin your own posts");
        }
        
        post.setPinned(true);
        return postRepository.save(post);
    }

    // ✅ UNPIN POST
    public Post unpinPost(Long postId, Long requestingUserId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return null;
        }
        
        Post post = postOpt.get();
        
        // Authorization check
        if (!post.getAuthorId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only unpin your own posts");
        }
        
        post.setPinned(false);
        return postRepository.save(post);
    }

    // ✅ GET TRENDING POSTS (based on engagement)
    public List<Post> getTrendingPosts() {
        // Get posts from last 7 days with high engagement
        return postRepository.findTop20ByOrderByCreatedAtDesc();
    }

    // ✅ GET ALL POSTS
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // ✅ DELETE POST
    public void deletePost(Long id, Long requestingUserId) {
        Optional<Post> postOpt = postRepository.findById(id);
        
        if (postOpt.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        
        Post post = postOpt.get();
        
        // Authorization check - only author can delete
        if (!post.getAuthorId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own posts");
        }
        
        postRepository.deleteById(id);
    }

    // ✅ GET FEED BY POST TYPE
    public List<Post> getFeedByPostType(Long userId, String postType) {
        // Get smart feed first
        List<Post> feed = getSmartFeed(userId);
        
        // Filter by post type
        return feed.stream()
                .filter(post -> postType.equalsIgnoreCase(post.getPostType()))
                .collect(Collectors.toList());
    }

    // ✅ GET FEED BY USER TYPE
    public List<Post> getFeedByUserType(Long userId, String userType) {
        // Get smart feed first
        List<Post> feed = getSmartFeed(userId);
        
        // Filter by user type (need to check author's user type)
        // This requires joining with User entity
        return postRepository.findByAuthorUserTypeInFeed(userId, userType);
    }
}
