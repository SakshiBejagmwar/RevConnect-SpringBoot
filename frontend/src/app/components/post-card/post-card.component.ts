import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Post, Comment } from '../../models/post.model';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css']
})
export class PostCardComponent implements OnInit {

  // Post data passed from parent component (Feed/Profile)
  @Input() post!: Post;

  // Event emitted to parent when post is deleted (so parent can remove it from list)
  @Output() postDeleted = new EventEmitter<number>();

  // Comments list and UI state for comments section
  comments: Comment[] = [];
  newCommentContent = '';
  showComments = false;

  // Post engagement stats + user like status
  likeCount = 0;
  commentCount = 0;
  isLiked = false;

  // Display names (author and original author if shared)
  authorName = '';
  originalAuthorName = '';

  // Edit post UI state
  isEditing = false;
  editedContent = '';

  constructor(
    private postService: PostService, // All post-related API calls
    private authService: AuthService, // Current user info (id/name)
    private userService: UserService  // Used to fetch user details (names)
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    this.loadAuthorName();     // Load author display name
    this.loadLikeCount();      // Load like count for this post
    this.loadCommentCount();   // Load comment count for this post
    this.checkIfLiked();       // Check if current user has liked it

    // If post is shared, load original post's author name
    if (this.post.isShared && this.post.originalPostId) {
      this.loadOriginalAuthor();
    }
  }

  // Fetch author name using authorId from post
  loadAuthorName(): void {
    this.userService.getUserById(this.post.authorId).subscribe({
      next: (user) => {
        this.authorName = user.name;
      },
      error: (error) => console.error('Error loading author:', error)
    });
  }

  // If the post is shared, fetch original post and its author name
  loadOriginalAuthor(): void {
    if (!this.post.originalPostId) return;

    this.postService.getPostById(this.post.originalPostId).subscribe({
      next: (originalPost) => {
        this.userService.getUserById(originalPost.authorId).subscribe({
          next: (user) => {
            this.originalAuthorName = user.name;
          },
          error: (error) => console.error('Error loading original author:', error)
        });
      },
      error: (error) => console.error('Error loading original post:', error)
    });
  }

  // Fetch like count from backend
  loadLikeCount(): void {
    if (this.post.id) {
      this.postService.getLikeCount(this.post.id).subscribe({
        next: (count) => {
          this.likeCount = count;
        },
        error: (error) => console.error('Error loading likes:', error)
      });
    }
  }

  // Fetch comment count from backend
  loadCommentCount(): void {
    if (this.post.id) {
      this.postService.getCommentCount(this.post.id).subscribe({
        next: (count) => {
          this.commentCount = count;
        },
        error: (error) => console.error('Error loading comment count:', error)
      });
    }
  }

  // Check whether current user already liked this post
  checkIfLiked(): void {
    if (this.post.id) {
      this.postService.checkIfLiked(this.post.id).subscribe({
        next: (liked) => {
          this.isLiked = liked;
        },
        error: (error) => console.error('Error checking like status:', error)
      });
    }
  }

  // Like/unlike post (toggle behavior)
  toggleLike(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId || !this.post.id) return;

    // If already liked -> unlike
    if (this.isLiked) {
      this.postService.unlikePost(userId, this.post.id).subscribe({
        next: () => {
          this.isLiked = false;
          this.likeCount--;
        },
        error: (error) => console.error('Error unliking post:', error)
      });

    // If not liked -> like
    } else {
      this.postService.likePost({ userId, postId: this.post.id }).subscribe({
        next: () => {
          this.isLiked = true;
          this.likeCount++;
        },
        error: (error) => console.error('Error liking post:', error)
      });
    }
  }

  // Show/hide comments section. If opening first time, load comments.
  toggleComments(): void {
    this.showComments = !this.showComments;

    // Load comments only once (basic optimization)
    if (this.showComments && this.comments.length === 0) {
      this.loadComments();
    }
  }

  // Load all comments for the post
  loadComments(): void {
    if (this.post.id) {
      this.postService.getCommentsByPost(this.post.id).subscribe({
        next: (comments) => {
          this.comments = comments;

          // For each comment, fetch commenter name (multiple API calls)
          this.comments.forEach(comment => {
            this.userService.getUserById(comment.userId).subscribe({
              next: (user) => {
                comment.userName = user.name; // Attach userName for UI
              }
            });
          });
        },
        error: (error) => console.error('Error loading comments:', error)
      });
    }
  }

  // Add a new comment
  addComment(): void {
    if (!this.newCommentContent.trim()) return;

    const userId = this.authService.getCurrentUserId();
    if (!userId || !this.post.id) return;

    const comment: Comment = {
      content: this.newCommentContent,
      userId: userId,
      postId: this.post.id
    };

    this.postService.addComment(comment).subscribe({
      next: (createdComment) => {
        // Set current user name locally to avoid extra API call
        createdComment.userName = this.authService.currentUser()?.name;

        // Update UI instantly
        this.comments.push(createdComment);
        this.newCommentContent = '';
        this.commentCount++;
      },
      error: (error) => console.error('Error adding comment:', error)
    });
  }

  // Start edit mode for post
  startEdit(): void {
    this.isEditing = true;
    this.editedContent = this.post.content;
  }

  // Cancel edit mode
  cancelEdit(): void {
    this.isEditing = false;
    this.editedContent = '';
  }

  // Save edited post content to backend
  saveEdit(): void {
    if (!this.editedContent.trim() || !this.post.id) return;

    // Create updated object with new content
    const updatedPost = { ...this.post, content: this.editedContent };

    this.postService.updatePost(this.post.id, updatedPost).subscribe({
      next: (updated) => {
        // Update UI content from backend response
        this.post.content = updated.content;
        this.isEditing = false;
        this.editedContent = '';
      },
      error: (error) => console.error('Error updating post:', error)
    });
  }

  // Delete post (only if confirmed)
  deletePost(): void {
    if (!this.post.id) return;

    if (confirm('Are you sure you want to delete this post?')) {
      this.postService.deletePost(this.post.id).subscribe({
        next: () => {
          // Notify parent so it can remove post from list
          this.postDeleted.emit(this.post.id);
        },
        error: (error) => console.error('Error deleting post:', error)
      });
    }
  }

  // Delete a comment (only if confirmed)
  deleteComment(commentId: number | undefined): void {
    if (!commentId) return;

    if (confirm('Are you sure you want to delete this comment?')) {
      this.postService.deleteComment(commentId).subscribe({
        next: () => {
          this.comments = this.comments.filter(c => c.id !== commentId);
          this.commentCount--;
        },
        error: (error) => console.error('Error deleting comment:', error)
      });
    }
  }

  // Allow delete post only for post author
  canDeletePost(): boolean {
    const currentUserId = this.authService.getCurrentUserId();
    return currentUserId === this.post.authorId;
  }

  // Allow edit post only for post author
  canEditPost(): boolean {
    const currentUserId = this.authService.getCurrentUserId();
    return currentUserId === this.post.authorId;
  }

  // Allow delete comment only for comment author
  canDeleteComment(comment: Comment): boolean {
    const currentUserId = this.authService.getCurrentUserId();
    return currentUserId === comment.userId;
  }

  // Convert ISO date string to "Just now / Xm ago / Xh ago / Xd ago"
  formatDate(date: string | undefined): string {
    if (!date) return '';

    const postDate = new Date(date);
    const now = new Date();

    const diffMs = now.getTime() - postDate.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;

    return postDate.toLocaleDateString();
  }

  // Share a post to current user's feed (creates a shared post)
  sharePost(): void {
    if (!this.post.id) return;

    if (confirm('Share this post to your feed?')) {
      this.postService.sharePost(this.post.id).subscribe({
        next: () => {
          alert('Post shared successfully!');
        },
        error: (error) => {
          console.error('Error sharing post:', error);
          alert('Failed to share post');
        }
      });
    }
  }

  // Pin post to profile
  pinPost(): void {
    if (!this.post.id) return;

    this.postService.pinPost(this.post.id).subscribe({
      next: () => {
        this.post.isPinned = true; // Update UI immediately
        alert('Post pinned to profile!');
      },
      error: (error) => {
        console.error('Error pinning post:', error);
        alert('Failed to pin post');
      }
    });
  }

  // Unpin post from profile
  unpinPost(): void {
    if (!this.post.id) return;

    this.postService.unpinPost(this.post.id).subscribe({
      next: () => {
        this.post.isPinned = false; // Update UI immediately
        alert('Post unpinned from profile!');
      },
      error: (error) => {
        console.error('Error unpinning post:', error);
        alert('Failed to unpin post');
      }
    });
  }

  // Convert CTA enum to readable button text
  getCallToActionText(): string {
    switch (this.post.callToAction) {
      case 'LEARN_MORE': return 'Learn More';
      case 'SHOP_NOW': return 'Shop Now';
      case 'CONTACT_US': return 'Contact Us';
      case 'SIGN_UP': return 'Sign Up';
      default: return '';
    }
  }

  // -----------------------
  // Media handling helpers
  // -----------------------

  // Returns true if media path is an image type (extension or URL)
  isImage(mediaPath: string): boolean {
    if (!mediaPath) return false;
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg'];
    const lowerPath = mediaPath.toLowerCase();

    // Check file extension
    if (imageExtensions.some(ext => lowerPath.endsWith(ext))) {
      return true;
    }

    // If URL and not video/document, treat as image
    if ((lowerPath.startsWith('http://') || lowerPath.startsWith('https://')) &&
        !this.isVideo(mediaPath) && !this.isDocument(mediaPath)) {
      return true;
    }

    return false;
  }

  // Returns true if media path is video type
  isVideo(mediaPath: string): boolean {
    if (!mediaPath) return false;
    const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.avi'];
    const lowerPath = mediaPath.toLowerCase();
    return videoExtensions.some(ext => lowerPath.endsWith(ext));
  }

  // Returns true if media path is document type
  isDocument(mediaPath: string): boolean {
    if (!mediaPath) return false;
    const docExtensions = ['.pdf', '.doc', '.docx', '.txt', '.xls', '.xlsx', '.ppt', '.pptx'];
    const lowerPath = mediaPath.toLowerCase();
    return docExtensions.some(ext => lowerPath.endsWith(ext));
  }

  // Builds final media URL: if local path => add backend base URL
  getMediaUrl(mediaPath: string): string {
    if (!mediaPath) return '';

    // Already a full URL
    if (mediaPath.startsWith('http://') || mediaPath.startsWith('https://')) {
      return mediaPath;
    }

    // Local media path served from backend
    return `http://localhost:8080${mediaPath}`;
  }

  // Extracts file name from full path to show in UI for documents
  getFileName(mediaPath: string): string {
    if (!mediaPath) return 'Document';
    const parts = mediaPath.split('/');
    return parts[parts.length - 1] || 'Document';
  }

  // If media fails to load, hide it
  onMediaError(event: any): void {
    console.error('Error loading media');
    event.target.style.display = 'none';
  }

  // Runs when media loads successfully (placeholder)
  onMediaLoad(event: any): void {
    // Media loaded successfully
  }
}