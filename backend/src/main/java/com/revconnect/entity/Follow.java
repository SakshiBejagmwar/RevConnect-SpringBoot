package com.revconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows",
       uniqueConstraints = @UniqueConstraint(columnNames = {"followerId", "followingId"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long followerId;   // who follows
    private Long followingId;  // whom they follow
    
    @Column(nullable = false)
    private String status = "ACCEPTED"; // PENDING, ACCEPTED, REJECTED
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Follow() {
        this.createdAt = LocalDateTime.now();
        this.status = "ACCEPTED";
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFollowerId() {
		return followerId;
	}
	public void setFollowerId(Long followerId) {
		this.followerId = followerId;
	}
	public Long getFollowingId() {
		return followingId;
	}
	public void setFollowingId(Long followingId) {
		this.followingId = followingId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
