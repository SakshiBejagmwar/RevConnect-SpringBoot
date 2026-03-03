package com.revconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tag;

    private Long usageCount = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Long usageCount) {
        this.usageCount = usageCount;
    }
}
