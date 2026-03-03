package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "creator_profiles")
public class CreatorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private String creatorName; // Business/Creator name

    private String category; // Category/Industry

    @Column(columnDefinition = "TEXT")
    private String detailedBio;

    private String contactInfo; // Contact information

    private String website; // Website link

    @Column(columnDefinition = "TEXT")
    private String socialMediaLinks; // JSON or comma separated (Instagram, Twitter, YouTube, etc.)

    @Column(columnDefinition = "TEXT")
    private String externalLinks; // Multiple external links for endorsements/partnerships

    private Long pinnedPostId;

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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetailedBio() {
        return detailedBio;
    }

    public void setDetailedBio(String detailedBio) {
        this.detailedBio = detailedBio;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(String socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public String getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(String externalLinks) {
        this.externalLinks = externalLinks;
    }

    public Long getPinnedPostId() {
        return pinnedPostId;
    }

    public void setPinnedPostId(Long pinnedPostId) {
        this.pinnedPostId = pinnedPostId;
    }
}
