package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findByUserId(Long userId);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Long countByPostId(Long postId);
}
