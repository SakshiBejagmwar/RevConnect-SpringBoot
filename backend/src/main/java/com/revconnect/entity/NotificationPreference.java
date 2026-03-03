package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    
    // Notification types
    private boolean likeEnabled = true;
    private boolean commentEnabled = true;
    private boolean shareEnabled = true;
    private boolean followEnabled = true;
    private boolean followRequestEnabled = true;
    private boolean connectionRequestEnabled = true;
    private boolean connectionAcceptedEnabled = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isLikeEnabled() {
        return likeEnabled;
    }

    public void setLikeEnabled(boolean likeEnabled) {
        this.likeEnabled = likeEnabled;
    }

    public boolean isCommentEnabled() {
        return commentEnabled;
    }

    public void setCommentEnabled(boolean commentEnabled) {
        this.commentEnabled = commentEnabled;
    }

    public boolean isShareEnabled() {
        return shareEnabled;
    }

    public void setShareEnabled(boolean shareEnabled) {
        this.shareEnabled = shareEnabled;
    }

    public boolean isFollowEnabled() {
        return followEnabled;
    }

    public void setFollowEnabled(boolean followEnabled) {
        this.followEnabled = followEnabled;
    }

    public boolean isFollowRequestEnabled() {
        return followRequestEnabled;
    }

    public void setFollowRequestEnabled(boolean followRequestEnabled) {
        this.followRequestEnabled = followRequestEnabled;
    }

    public boolean isConnectionRequestEnabled() {
        return connectionRequestEnabled;
    }

    public void setConnectionRequestEnabled(boolean connectionRequestEnabled) {
        this.connectionRequestEnabled = connectionRequestEnabled;
    }

    public boolean isConnectionAcceptedEnabled() {
        return connectionAcceptedEnabled;
    }

    public void setConnectionAcceptedEnabled(boolean connectionAcceptedEnabled) {
        this.connectionAcceptedEnabled = connectionAcceptedEnabled;
    }
}
