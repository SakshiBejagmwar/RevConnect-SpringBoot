package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.ScheduledPost;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledPostRepository extends JpaRepository<ScheduledPost, Long> {
    
    List<ScheduledPost> findByUserIdAndPostedFalse(Long userId);
    
    List<ScheduledPost> findByScheduledTimeBeforeAndPostedFalse(LocalDateTime time);
    
    List<ScheduledPost> findByUserId(Long userId);
}
