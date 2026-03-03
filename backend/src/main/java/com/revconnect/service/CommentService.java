package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AnalyticsService analyticsService;

    // ✅ ADD COMMENT
    public Comment addComment(Long userId, Long postId, String content) {
        // Validate post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Validate content
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Comment content cannot be empty");
        }
        if (content.length() > 2000) {
            throw new RuntimeException("Comment content cannot exceed 2000 characters");
        }

        // Create comment
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setContent(content);

        Comment savedComment = commentRepository.save(comment);

        // Increment analytics
        analyticsService.incrementComments(postId);

        // Create notification (only if not commenting on own post)
        if (!post.getAuthorId().equals(userId)) {
            notificationService.createNotification(
                    userId,
                    post.getAuthorId(),
                    "COMMENT",
                    post.getId(),
                    "Someone commented on your post"
            );
        }

        return savedComment;
    }

    // ✅ GET COMMENTS BY POST
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }
    
    // ✅ GET COMMENT COUNT
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    // ✅ GET COMMENT BY ID
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    // ✅ GET USER'S COMMENTS
    public List<Comment> getUserComments(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ✅ DELETE COMMENT (with authorization)
    public void deleteComment(Long id, Long requestingUserId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Authorization check - only comment author can delete
        if (!comment.getUserId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own comments");
        }

        commentRepository.delete(comment);

        // Decrement analytics
        analyticsService.decrementComments(comment.getPostId());
    }
}
