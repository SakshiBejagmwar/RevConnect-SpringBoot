import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService, PostAnalytics } from '../../services/analytics.service';
import { PostService } from '../../services/post.service';
import { FollowService } from '../../services/follow.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Post } from '../../models/post.model';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {

  // Stores posts created by the currently logged-in user
  myPosts: Post[] = [];

  // Holds the currently selected post (user clicks a post from list)
  selectedPost: Post | null = null;

  // Stores analytics details for the selected post
  postAnalytics: PostAnalytics | null = null;

  // Stores engagement rate value returned by backend
  engagementRate = 0;

  // Used to show/hide loader on UI during API calls
  isLoading = false;

  // Total followers count for current user
  totalFollowers = 0;

  // Stores follower counts by role (demographics)
  followersByRole: any = {
    USER: 0,
    CREATOR: 0,
    BUSINESS: 0
  };

  // Overall engagement metrics (currently reset, can be calculated later)
  totalLikes = 0;
  totalComments = 0;
  totalShares = 0;
  totalReach = 0;
  averageEngagement = 0;

  constructor(
    private analyticsService: AnalyticsService,
    private postService: PostService,
    private followService: FollowService,
    private authService: AuthService,
    private userService: UserService
  ) {}

  // Runs automatically when component loads
  ngOnInit(): void {
    // Load required data for analytics page
    this.loadMyPosts();
    this.loadFollowerDemographics();
    this.loadOverallMetrics();
  }

  // Loads the logged-in user's posts from backend
  loadMyPosts(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return; // If userId is missing, do nothing

    // NOTE: The method name getPostById suggests it returns a post by postId,
    // but here you're passing userId and expecting posts. Verify backend method naming.
    this.postService.getPostById(userId).subscribe({
      next: (posts: any) => {
        // Ensure result is always treated as an array
        this.myPosts = Array.isArray(posts) ? posts : [posts];
      },
      error: (error) => console.error('Error loading posts:', error)
    });
  }

  // Loads follower list and counts followers by their roles (USER/CREATOR/BUSINESS)
  loadFollowerDemographics(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    // Get followers for the logged-in user
    this.followService.getFollowers(userId).subscribe({
      next: (followers) => {
        // Save total followers count
        this.totalFollowers = followers.length;

        // For each follower, fetch their user details to get their role
        // (This makes multiple API calls; can be optimized using backend aggregation)
        followers.forEach(follow => {
          this.userService.getUserById(follow.followerId).subscribe({
            next: (user) => {
              // Increment role count if role exists
              if (user.role) {
                this.followersByRole[user.role]++;
              }
            },
            error: (error) => console.error('Error loading follower details:', error)
          });
        });
      },
      error: (error) => console.error('Error loading followers:', error)
    });
  }

  // Loads overall metrics for the user (currently just reset placeholders)
  loadOverallMetrics(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    // Ideally this should be one backend API call returning total stats
    // For now just reset values (can be implemented later)
    this.totalLikes = 0;
    this.totalComments = 0;
    this.totalShares = 0;
    this.totalReach = 0;
  }

  // Runs when user selects a post from UI
  selectPost(post: Post): void {
    this.selectedPost = post;

    // If post has id, load analytics for that post
    if (post.id) {
      this.loadPostAnalytics(post.id);
    }
  }

  // Loads analytics + engagement rate for a specific post
  loadPostAnalytics(postId: number): void {
    this.isLoading = true; // Show loader while loading analytics

    // API call 1: Get post analytics object (likes, comments, shares, etc.)
    this.analyticsService.getPostAnalytics(postId).subscribe({
      next: (analytics) => {
        this.postAnalytics = analytics;
        this.isLoading = false; // Hide loader
      },
      error: (error) => {
        console.error('Error loading post analytics:', error);
        this.isLoading = false; // Hide loader even on error
      }
    });

    // API call 2: Get engagement rate for the post
    this.analyticsService.getEngagementRate(postId).subscribe({
      next: (response) => {
        this.engagementRate = response.engagementRate;
      },
      error: (error) => console.error('Error loading engagement rate:', error)
    });
  }

  // Returns role-wise follower percentage (used in UI for charts/progress bars)
  getFollowerPercentage(role: string): number {
    if (this.totalFollowers === 0) return 0;

    // Example: (CREATOR followers / totalFollowers) * 100
    return Math.round((this.followersByRole[role] / this.totalFollowers) * 100);
  }
}