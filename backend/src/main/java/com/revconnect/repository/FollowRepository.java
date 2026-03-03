package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findByFollowerIdAndStatus(Long followerId, String status);   // following list
    
    List<Follow> findByFollowingIdAndStatus(Long followingId, String status); // followers list

    List<Follow> findByFollowerId(Long followerId);   // all following (any status)

    List<Follow> findByFollowingId(Long followingId); // all followers (any status)

    Long countByFollowingIdAndStatus(Long followingId, String status);

    Long countByFollowerIdAndStatus(Long followerId, String status);
}
