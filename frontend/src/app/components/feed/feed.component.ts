import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';

/**
 * Local interface used to represent hashtags shown in the UI.
 * (Not fully used yet in the current implementation.)
 */
interface Hashtag {
  id: number;
  tag: string;
  usageCount: number;
}

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, PostCardComponent],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  // Feed posts shown on the page
  posts: Post[] = [];

  // Trending hashtags list (placeholder - not populated yet)
  trendingHashtags: Hashtag[] = [];

  // New post form: content text
  newPostContent = '';

  // UI state flags/messages
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Advanced post creation options
  postType: string = 'REGULAR';      // e.g., REGULAR / PROMOTION / etc.
  callToAction: string = '';         // Used mainly for business posts
  scheduleDate: string = '';         // Date part for scheduling
  scheduleTime: string = '';         // Time part for scheduling
  showAdvancedOptions = false;       // Toggle advanced UI block

  // File upload state
  selectedFile: File | null = null;          // File chosen from input
  selectedFilePreview: string | null = null; // Base64 preview for images
  uploadedFilePath: string | null = null;    // Backend path after upload
  mediaUrl: string = '';                     // External media URL (optional)
  isUploading = false;                       // Upload spinner flag

  // Feed filters
  selectedFeedType: 'all' | 'trending' = 'all';
  selectedPostType: string = 'all';
  selectedUserType: string = 'all';
  showFilters = false;

  constructor(
    private postService: PostService, // Handles all post-related API calls
    private authService: AuthService  // Used to get current logged-in user id
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    this.loadFeed();             // Load initial feed posts
    this.loadTrendingHashtags(); // Load trending hashtags (currently placeholder)
  }

  /**
   * Loads feed based on current selection:
   * - Trending feed OR
   * - Filter by post type OR
   * - Filter by user type OR
   * - Default feed for the logged-in user
   */
  loadFeed(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.isLoading = true;

    // Load trending posts if user selected "trending"
    if (this.selectedFeedType === 'trending') {
      this.loadTrendingPosts();

    // Load feed filtered by post type
    } else if (this.selectedPostType !== 'all') {
      this.loadFeedByPostType();

    // Load feed filtered by user type
    } else if (this.selectedUserType !== 'all') {
      this.loadFeedByUserType();

    // Default: load normal feed
    } else {
      this.postService.getFeed(userId).subscribe({
        next: (posts) => {
          this.posts = posts;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading feed:', error);
          this.isLoading = false;
        }
      });
    }
  }

  // Loads trending posts from backend
  loadTrendingPosts(): void {
    this.postService.getTrendingPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading trending posts:', error);
        this.isLoading = false;
      }
    });
  }

  // Loads feed filtered by selected post type
  loadFeedByPostType(): void {
    this.postService.getFeedByPostType(this.selectedPostType).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading feed by post type:', error);
        this.isLoading = false;
      }
    });
  }

  // Loads feed filtered by selected user type (USER/CREATOR/BUSINESS)
  loadFeedByUserType(): void {
    this.postService.getFeedByUserType(this.selectedUserType).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading feed by user type:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * Placeholder for trending hashtags.
   * Currently calling search endpoint with empty string and not using response.
   * Ideally should call a dedicated backend endpoint like: GET /hashtags/trending
   */
  loadTrendingHashtags(): void {
    this.postService.searchByHashtag('').subscribe({
      next: () => {
        // TODO: populate trendingHashtags properly from backend
      },
      error: (error) => console.error('Error loading trending hashtags:', error)
    });
  }

  // Switch between "all" feed and "trending" feed
  setFeedType(type: 'all' | 'trending'): void {
    this.selectedFeedType = type;

    // Reset filters when switching feed type
    this.selectedPostType = 'all';
    this.selectedUserType = 'all';

    this.loadFeed();
  }

  // Apply filters (post type / user type)
  applyFilters(): void {
    this.selectedFeedType = 'all'; // Filters apply to normal feed mode
    this.loadFeed();
  }

  // Reset all filters and reload feed
  clearFilters(): void {
    this.selectedPostType = 'all';
    this.selectedUserType = 'all';
    this.selectedFeedType = 'all';
    this.loadFeed();
  }

  // Toggle filter panel visibility
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // Search feed by hashtag tag (e.g., "#angular")
  searchHashtag(tag: string): void {
    this.isLoading = true;
    this.postService.searchByHashtag(tag).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching hashtag:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * Runs when user selects a file from input.
   * If file is an image, creates a base64 preview using FileReader.
   */
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;

      // Create preview for images only
      if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.selectedFilePreview = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        this.selectedFilePreview = null;
      }
    }
  }

  // Removes selected file and clears related media inputs
  removeFile(): void {
    this.selectedFile = null;
    this.selectedFilePreview = null;
    this.uploadedFilePath = null;
    this.mediaUrl = '';
  }

  /**
   * Uploads selected file to backend and returns uploaded file path.
   * Wraps observable in a Promise so it can be awaited inside async functions.
   */
  uploadFile(): Promise<string | null> {
    return new Promise((resolve, reject) => {
      if (!this.selectedFile) {
        resolve(null);
        return;
      }

      this.isUploading = true;

      this.postService.uploadFile(this.selectedFile).subscribe({
        next: (response) => {
          this.isUploading = false;
          this.uploadedFilePath = response.filePath; // Save backend file path
          resolve(response.filePath);
        },
        error: (error) => {
          this.isUploading = false;
          this.errorMessage = 'Failed to upload file';
          console.error('Error uploading file:', error);
          reject(error);
        }
      });
    });
  }

  /**
   * Creates a post:
   * - Validates that at least one of (content/file/mediaUrl) is present
   * - If scheduling fields exist -> schedules post (currently calls createPost too)
   * - Otherwise uploads file (if needed) and calls createPost API
   */
  async createPost(): Promise<void> {
    // Must have at least text OR file OR media URL
    if (!this.newPostContent.trim() && !this.selectedFile && !this.mediaUrl.trim()) {
      this.errorMessage = 'Post must have content, file, or media URL';
      return;
    }

    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    // If scheduling date+time exists, call schedule flow instead
    if (this.scheduleDate && this.scheduleTime) {
      await this.schedulePost();
      return;
    }

    try {
      // Upload file if user selected one and it's not uploaded yet
      let mediaPath = this.uploadedFilePath;
      if (this.selectedFile && !mediaPath) {
        mediaPath = await this.uploadFile();
      }

      // If user provided external media URL, prefer that
      if (this.mediaUrl.trim()) {
        mediaPath = this.mediaUrl.trim();
      }

      // Construct post payload
      const post: Post = {
        content: this.newPostContent || '',
        authorId: userId,
        postType: this.postType,
        callToAction: this.callToAction || undefined,
        mediaPath: mediaPath || undefined
      };

      // Send create post request
      this.postService.createPost(post).subscribe({
        next: (createdPost) => {
          // Add new post at top of feed
          this.posts.unshift(createdPost);

          // Reset UI form after success
          this.resetPostForm();

          this.successMessage = 'Post created successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to create post';
          console.error('Error creating post:', error);
        }
      });
    } catch (error) {
      this.errorMessage = 'Failed to create post';
      console.error('Error:', error);
    }
  }

  /**
   * Schedules a post using schedule date/time fields.
   * NOTE: You build a "scheduledPost" object but currently you are still calling createPost().
   * Ideally you should call a dedicated backend endpoint like: postService.schedulePost(scheduledPost).
   */
  async schedulePost(): Promise<void> {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    const scheduledDateTime = `${this.scheduleDate}T${this.scheduleTime}`;

    try {
      // Upload file if needed
      let mediaPath = this.uploadedFilePath;
      if (this.selectedFile && !mediaPath) {
        mediaPath = await this.uploadFile();
      }

      // Use external media URL if provided
      if (this.mediaUrl.trim()) {
        mediaPath = this.mediaUrl.trim();
      }

      // Prepared scheduled post payload (currently not used in API call)
      const scheduledPost = {
        userId: userId,
        content: this.newPostContent || '',
        scheduledTime: scheduledDateTime,
        postType: this.postType,
        callToAction: this.callToAction || undefined,
        mediaPath: mediaPath || undefined
      };

      // TODO: call a proper schedule endpoint using scheduledPost
      // For now, it calls createPost() immediately (not true scheduling)
      this.postService.createPost({
        content: this.newPostContent || '',
        authorId: userId,
        postType: this.postType,
        callToAction: this.callToAction || undefined,
        mediaPath: mediaPath || undefined
      }).subscribe({
        next: () => {
          this.resetPostForm();
          this.successMessage = 'Post scheduled successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = 'Failed to schedule post';
          console.error('Error scheduling post:', error);
        }
      });
    } catch (error) {
      this.errorMessage = 'Failed to schedule post';
      console.error('Error:', error);
    }
  }

  // Resets all fields of the create-post form back to defaults
  resetPostForm(): void {
    this.newPostContent = '';
    this.postType = 'REGULAR';
    this.callToAction = '';
    this.scheduleDate = '';
    this.scheduleTime = '';
    this.showAdvancedOptions = false;

    // Reset media/file inputs
    this.selectedFile = null;
    this.selectedFilePreview = null;
    this.uploadedFilePath = null;
    this.mediaUrl = '';
  }

  // Toggle advanced post creation UI block
  toggleAdvancedOptions(): void {
    this.showAdvancedOptions = !this.showAdvancedOptions;
  }

  // Called from PostCardComponent when a post is deleted
  onPostDeleted(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
  }

  // If media fails to load in UI, hide that element
  onMediaError(event: any): void {
    event.target.style.display = 'none';
  }
}