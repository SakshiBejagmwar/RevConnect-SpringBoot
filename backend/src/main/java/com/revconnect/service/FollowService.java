package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.entity.Follow;
import com.revconnect.entity.User;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {
	
    @Autowired
    private NotificationService notificationService;
	
    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private UserRepository userRepository;

    // ✅ FOLLOW USER (with private account support)
    @Transactional
    public Follow followUser(Long followerId, Long followingId) {

        // 1. Validation: Prevent self-following
        if (followerId.equals(followingId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        // 2. Validation: Check if relationship already exists
        Optional<Follow> existingFollow = followRepository
                .findByFollowerIdAndFollowingId(followerId, followingId);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("Already following or request pending");
        }

        // 3. Check if the user being followed has a PRIVATE account
        Optional<User> userToFollow = userRepository.findById(followingId);
        
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        
        if (userToFollow.isPresent() && "PRIVATE".equals(userToFollow.get().getAccountPrivacy())) {
            // For PRIVATE accounts, set status to PENDING
            follow.setStatus("PENDING");
            Follow savedFollow = followRepository.save(follow);
            
            // Send notification about follow request
            notificationService.createNotification(
                    followerId,
                    followingId,
                    "FOLLOW_REQUEST",
                    null,
                    "Someone sent you a follow request"
            );
            
            return savedFollow;
        } else {
            // For PUBLIC accounts, auto-accept
            follow.setStatus("ACCEPTED");
            Follow savedFollow = followRepository.save(follow);
            
            // Send notification about new follower
            notificationService.createNotification(
                    followerId,
                    followingId,
                    "FOLLOW",
                    null,
                    "Someone started following you"
            );
            
            return savedFollow;
        }
    }
    
    // ✅ ACCEPT FOLLOW REQUEST
    // ✅ ACCEPT FOLLOW REQUEST
    @Transactional
    public Follow acceptFollowRequest(Long followId, Long authenticatedUserId) {
        Optional<Follow> followOpt = followRepository.findById(followId);
        if (followOpt.isPresent()) {
            Follow follow = followOpt.get();

            // Authorization: Only the user being followed can accept the request
            if (!follow.getFollowingId().equals(authenticatedUserId)) {
                throw new RuntimeException("You are not authorized to accept this follow request");
            }

            follow.setStatus("ACCEPTED");
            Follow updated = followRepository.save(follow);

            // Notify the follower that their request was accepted
            notificationService.createNotification(
                    follow.getFollowingId(),
                    follow.getFollowerId(),
                    "FOLLOW_ACCEPTED",
                    null,
                    "Your follow request was accepted"
            );

            return updated;
        }
        throw new RuntimeException("Follow request not found");
    }

    
    // ✅ REJECT FOLLOW REQUEST
    // ✅ REJECT FOLLOW REQUEST
    @Transactional
    public void rejectFollowRequest(Long followId, Long authenticatedUserId) {
        Optional<Follow> followOpt = followRepository.findById(followId);
        if (followOpt.isPresent()) {
            Follow follow = followOpt.get();

            // Authorization: Only the user being followed can reject the request
            if (!follow.getFollowingId().equals(authenticatedUserId)) {
                throw new RuntimeException("You are not authorized to reject this follow request");
            }

            followRepository.deleteById(followId);
        } else {
            throw new RuntimeException("Follow request not found");
        }
    }

    
    // ✅ GET PENDING FOLLOW REQUESTS
    public List<Follow> getPendingFollowRequests(Long userId) {
        return followRepository.findByFollowingIdAndStatus(userId, "PENDING");
    }

    // ✅ UNFOLLOW USER
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(followRepository::delete);
    }

    // ✅ GET FOLLOWING LIST (People the user follows - ACCEPTED only)
    public List<Follow> getFollowing(Long userId) {
        return followRepository.findByFollowerIdAndStatus(userId, "ACCEPTED");
    }

    // ✅ GET FOLLOWERS LIST (People following the user - ACCEPTED only)
    public List<Follow> getFollowers(Long userId) {
        return followRepository.findByFollowingIdAndStatus(userId, "ACCEPTED");
    }

    // ✅ COUNT FOLLOWERS (ACCEPTED only)
    public Long countFollowers(Long userId) {
        return followRepository.countByFollowingIdAndStatus(userId, "ACCEPTED");
    }

    // ✅ COUNT FOLLOWING (ACCEPTED only)
    public Long countFollowing(Long userId) {
        return followRepository.countByFollowerIdAndStatus(userId, "ACCEPTED");
    }
    
    // ✅ CHECK IF FOLLOWING
    public boolean isFollowing(Long followerId, Long followingId) {
        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        return follow.isPresent() && "ACCEPTED".equals(follow.get().getStatus());
    }
}
