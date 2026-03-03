package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_analytics")
public class PostAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private Long totalLikes = 0L;
    private Long totalComments = 0L;
    private Long totalShares = 0L;
    private Long reach = 0L;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public Long getTotalLikes() {
		return totalLikes;
	}
	public void setTotalLikes(Long totalLikes) {
		this.totalLikes = totalLikes;
	}
	public Long getTotalComments() {
		return totalComments;
	}
	public void setTotalComments(Long totalComments) {
		this.totalComments = totalComments;
	}
	public Long getTotalShares() {
		return totalShares;
	}
	public void setTotalShares(Long totalShares) {
		this.totalShares = totalShares;
	}
	public Long getReach() {
		return reach;
	}
	public void setReach(Long reach) {
		this.reach = reach;
	}

    // Getters and Setters
    
    
}
