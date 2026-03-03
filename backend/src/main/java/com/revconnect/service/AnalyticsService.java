package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.repository.PostAnalyticsRepository;

@Service
public class AnalyticsService {

    @Autowired
    private PostAnalyticsRepository repository;

    public void initializeAnalytics(Long postId) {
        PostAnalytics analytics = new PostAnalytics();
        analytics.setPostId(postId);
        repository.save(analytics);
    }

    public void incrementLikes(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
        repository.save(analytics);
    }

    public void incrementComments(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalComments(analytics.getTotalComments() + 1);
        repository.save(analytics);
    }

    public void incrementShares(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalShares(analytics.getTotalShares() + 1);
        repository.save(analytics);
    }
    
    public void decrementShares(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalShares(analytics.getTotalShares() - 1);
        repository.save(analytics);
    }

    public void incrementReach(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setReach(analytics.getReach() + 1);
        repository.save(analytics);
    }
    
    public void decrementLikes(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalLikes(analytics.getTotalLikes() - 1);
        repository.save(analytics);
    }
    
    public void decrementComments(Long postId) {
        PostAnalytics analytics = repository.findByPostId(postId).orElseThrow();
        analytics.setTotalComments(analytics.getTotalComments() - 1);
        repository.save(analytics);
    }

    public PostAnalytics getAnalytics(Long postId) {
        return repository.findByPostId(postId).orElse(null);
    }

    public double calculateEngagement(Long postId) {
        PostAnalytics a = repository.findByPostId(postId).orElseThrow();
        if (a.getReach() == 0) return 0;
        return (double)(a.getTotalLikes() + a.getTotalComments() + a.getTotalShares()) / a.getReach();
    }
}
