package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Like;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByPostId(Long postId);

    List<Like> findByUserId(Long userId);

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    Long countByPostId(Long postId);
}
