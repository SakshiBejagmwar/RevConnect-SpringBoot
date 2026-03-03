package com.revconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Long originalPostId; // for repost reference
    private boolean isShared = false;
    
    // New fields for enhanced features
    private String postType; // REGULAR, PROMOTIONAL, ANNOUNCEMENT
    private String callToAction; // LEARN_MORE, SHOP_NOW, CONTACT_US, SIGN_UP, null
    private String mediaPath; // Path to image/video
    private boolean isPinned = false;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_hashtags",
        joinColumns = @JoinColumn(name = "post_ref_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_ref_id")
    )
    private java.util.List<Hashtag> hashtags = new java.util.ArrayList<>();

    public Post() {
        this.createdAt = LocalDateTime.now();
        this.postType = "REGULAR";
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (postType == null) {
            postType = "REGULAR";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }

    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getCallToAction() { return callToAction; }
    public void setCallToAction(String callToAction) { this.callToAction = callToAction; }

    public String getMediaPath() { return mediaPath; }
    public void setMediaPath(String mediaPath) { this.mediaPath = mediaPath; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public java.util.List<Hashtag> getHashtags() { return hashtags; }
    public void setHashtags(java.util.List<Hashtag> hashtags) { this.hashtags = hashtags; }
}