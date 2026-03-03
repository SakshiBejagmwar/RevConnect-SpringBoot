package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_hashtags")
public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private Long hashtagId;
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
	public Long getHashtagId() {
		return hashtagId;
	}
	public void setHashtagId(Long hashtagId) {
		this.hashtagId = hashtagId;
	}

    // Getters and Setters
    
}
