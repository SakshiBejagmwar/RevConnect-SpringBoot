package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "business_profiles")
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private String businessName; // Business name

    private String industry; // Category/Industry

    @Column(columnDefinition = "TEXT")
    private String detailedBio;

    private String contactInfo; // Contact information

    private String website; // Website link

    @Column(columnDefinition = "TEXT")
    private String socialMediaLinks; // JSON or comma separated

    @Column(columnDefinition = "TEXT")
    private String businessAddress; // Business address

    private String businessHours; // Business hours

    @Column(columnDefinition = "TEXT")
    private String externalLinks; // Multiple external links for endorsements/partnerships

    @Column(columnDefinition = "TEXT")
    private String productsDescription; // Showcase products/services

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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
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

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(String externalLinks) {
        this.externalLinks = externalLinks;
    }

    public String getProductsDescription() {
        return productsDescription;
    }

    public void setProductsDescription(String productsDescription) {
        this.productsDescription = productsDescription;
    }
}
