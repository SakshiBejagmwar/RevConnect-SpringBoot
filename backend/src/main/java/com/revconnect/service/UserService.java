package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.FollowRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register user
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    // Get all users (respects privacy)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID with privacy enforcement
    public Optional<User> getUserById(Long id, Long requestingUserId) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // If profile is private and requester is not the owner
        if ("PRIVATE".equals(user.getAccountPrivacy()) && !id.equals(requestingUserId)) {
            // Check if they are connected or following
            boolean isConnected = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(requestingUserId, id, "ACCEPTED")
                .isPresent() ||
                connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(id, requestingUserId, "ACCEPTED")
                .isPresent();
            
            boolean isFollowing = followRepository
                .findByFollowerIdAndFollowingId(requestingUserId, id)
                .isPresent();
            
            if (!isConnected && !isFollowing) {
                // Return limited profile
                User limitedUser = new User();
                limitedUser.setId(user.getId());
                limitedUser.setUsername(user.getUsername());
                limitedUser.setName(user.getName());
                limitedUser.setProfilePicturePath(user.getProfilePicturePath());
                limitedUser.setAccountPrivacy("PRIVATE");
                return Optional.of(limitedUser);
            }
        }
        
        // Remove password before returning
        user.setPassword(null);
        return Optional.of(user);
    }

    // Update user profile
    public User updateUser(Long id, User updatedUser, Long requestingUserId) {
        // Authorization check
        if (!id.equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only update your own profile");
        }
        
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            
            // Update allowed fields
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
            }
            if (updatedUser.getBio() != null) {
                user.setBio(updatedUser.getBio());
            }
            if (updatedUser.getLocation() != null) {
                user.setLocation(updatedUser.getLocation());
            }
            if (updatedUser.getWebsite() != null) {
                user.setWebsite(updatedUser.getWebsite());
            }
            if (updatedUser.getProfilePicturePath() != null) {
                user.setProfilePicturePath(updatedUser.getProfilePicturePath());
            }
            if (updatedUser.getAccountPrivacy() != null) {
                user.setAccountPrivacy(updatedUser.getAccountPrivacy());
            }
            
            User saved = userRepository.save(user);
            saved.setPassword(null);
            return saved;
        }
        return null;
    }

    // Change password
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }

    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Search users by name or username
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }
}
