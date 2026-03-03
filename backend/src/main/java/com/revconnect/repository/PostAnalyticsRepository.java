package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.PostAnalytics;
import java.util.Optional;

public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {

    Optional<PostAnalytics> findByPostId(Long postId);
}
