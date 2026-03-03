package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revconnect.entity.Notification;
import com.revconnect.entity.NotificationPreference;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.NotificationPreferenceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    // Create notification (with preference check)
    public Notification createNotification(Long senderId,
                                           Long receiverId,
                                           String type,
                                           Long postId,
                                           String message) {

        // Check if user has disabled this notification type
        if (!isNotificationEnabled(receiverId, type)) {
            return null; // Don't create notification if disabled
        }

        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setType(type);
        notification.setPostId(postId);
        notification.setMessage(message);

        return notificationRepository.save(notification);
    }

    // Check if notification type is enabled for user
    private boolean isNotificationEnabled(Long userId, String type) {
        Optional<NotificationPreference> prefOpt = preferenceRepository.findByUserId(userId);
        
        if (prefOpt.isEmpty()) {
            return true; // Default: all notifications enabled
        }
        
        NotificationPreference pref = prefOpt.get();
        
        switch (type.toUpperCase()) {
            case "LIKE":
                return pref.isLikeEnabled();
            case "COMMENT":
                return pref.isCommentEnabled();
            case "SHARE":
                return pref.isShareEnabled();
            case "FOLLOW":
                return pref.isFollowEnabled();
            case "FOLLOW_REQUEST":
                return pref.isFollowRequestEnabled();
            case "FOLLOW_ACCEPTED":
                return pref.isFollowEnabled();
            case "CONNECTION_REQUEST":
                return pref.isConnectionRequestEnabled();
            case "CONNECTION_ACCEPTED":
                return pref.isConnectionAcceptedEnabled();
            default:
                return true;
        }
    }

    // Get user notifications (ordered by newest first)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    // Get unread notification count
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByReceiverIdAndIsRead(userId, false);
    }

    // Mark notification as read (with authorization)
    @Transactional
    public void markAsRead(Long notificationId, Long authenticatedUserId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Authorization: Only the receiver can mark notification as read
        if (!notification.getReceiverId().equals(authenticatedUserId)) {
            throw new RuntimeException("You are not authorized to mark this notification as read");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Mark all notifications as read for a user
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByReceiverIdAndIsRead(userId, false);
        
        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }

    // Get notification preferences
    public NotificationPreference getPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Create default preferences
                    NotificationPreference pref = new NotificationPreference();
                    pref.setUserId(userId);
                    return preferenceRepository.save(pref);
                });
    }

    // Update notification preferences
    @Transactional
    public NotificationPreference updatePreferences(Long userId, NotificationPreference preferences) {
        NotificationPreference existing = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference pref = new NotificationPreference();
                    pref.setUserId(userId);
                    return pref;
                });
        
        // Update preferences
        existing.setLikeEnabled(preferences.isLikeEnabled());
        existing.setCommentEnabled(preferences.isCommentEnabled());
        existing.setShareEnabled(preferences.isShareEnabled());
        existing.setFollowEnabled(preferences.isFollowEnabled());
        existing.setFollowRequestEnabled(preferences.isFollowRequestEnabled());
        existing.setConnectionRequestEnabled(preferences.isConnectionRequestEnabled());
        existing.setConnectionAcceptedEnabled(preferences.isConnectionAcceptedEnabled());
        
        return preferenceRepository.save(existing);
    }
}
