package com.revconnect.repository;

import com.revconnect.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts by author
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Find posts by multiple authors (for feed)
    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);

    // Search posts by content
    List<Post> findByContentContainingIgnoreCase(String keyword);

    // Find posts by hashtag
    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.tag = :tag")
    List<Post> findByHashtag(@Param("tag") String tag);

    // Get trending posts (top 20 recent)
    List<Post> findTop20ByOrderByCreatedAtDesc();

    // Get pinned posts by author
    List<Post> findByAuthorIdAndIsPinnedTrue(Long authorId);

    // Get posts by type
    List<Post> findByPostTypeOrderByCreatedAtDesc(String postType);

    // Find posts by user type in feed (connections and follows)
    @Query("SELECT p FROM Post p JOIN User u ON p.authorId = u.id " +
           "WHERE u.role = :userType AND p.authorId IN " +
           "(SELECT c.receiverId FROM Connection c WHERE c.senderId = :userId AND c.status = 'ACCEPTED' " +
           "UNION SELECT c.senderId FROM Connection c WHERE c.receiverId = :userId AND c.status = 'ACCEPTED' " +
           "UNION SELECT f.followingId FROM Follow f WHERE f.followerId = :userId AND f.status = 'ACCEPTED' " +
           "UNION SELECT :userId) " +
           "ORDER BY p.createdAt DESC")
    List<Post> findByAuthorUserTypeInFeed(@Param("userId") Long userId, @Param("userType") String userType);
}
