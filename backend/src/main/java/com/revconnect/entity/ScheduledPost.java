package com.revconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_posts")
public class ScheduledPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime scheduledTime;

    private boolean posted = false;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public boolean isPosted() {
		return posted;
	}

	public void setPosted(boolean posted) {
		this.posted = posted;
	}

    // Getters and Setters
    
    
}
