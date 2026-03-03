import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { FollowService } from '../../services/follow.service';
import { ConnectionService } from '../../services/connection.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  // Profile user details (the user whose profile is being viewed)
  user: User | null = null;

  // True if currently logged-in user is viewing their own profile
  isOwnProfile = false;

  // Controls edit mode UI
  isEditing = false;

  // Temporary object used while editing (so original user data is not modified directly)
  editedUser: Partial<User> = {};

  // Followers & following counts shown on profile
  followersCount = 0;
  followingCount = 0;

  // True if current user is following this profile user
  isFollowing = false;

  // UI feedback messages
  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,            // To read profile user id from URL
    private userService: UserService,         // API calls for user data
    private authService: AuthService,         // Current logged-in user info
    private followService: FollowService,     // Follow/unfollow + follow status
    private connectionService: ConnectionService // Send connection requests
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    // Subscribe to route params (profile/:id)
    this.route.params.subscribe(params => {
      const userId = +params['id']; // Convert route param to number
      this.loadProfile(userId);
    });
  }

  // Loads profile data from backend
  loadProfile(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.user = user;

        // Determine if the profile belongs to current user
        this.isOwnProfile = this.authService.getCurrentUserId() === userId;

        // Load followers/following counts
        this.loadFollowStats(userId);

        // If viewing someone else, check follow status
        if (!this.isOwnProfile) {
          this.checkFollowStatus(userId);
        }
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Failed to load profile';
      }
    });
  }

  // Check if current user follows this profile user
  checkFollowStatus(userId: number): void {
    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId) return;

    this.followService.checkFollowStatus(currentUserId, userId).subscribe({
      next: (response) => {
        this.isFollowing = response.isFollowing;
      },
      error: (error) => {
        console.error('Error checking follow status:', error);
      }
    });
  }

  // Loads follower and following count for the profile user
  loadFollowStats(userId: number): void {
    this.followService.getFollowers(userId).subscribe({
      next: (followers) => {
        this.followersCount = followers.length;
      }
    });

    this.followService.getFollowing(userId).subscribe({
      next: (following) => {
        this.followingCount = following.length;
      }
    });
  }

  /**
   * Toggle edit mode.
   * - If currently editing => save changes
   * - Else => start editing (copy current user data to editedUser)
   */
  toggleEdit(): void {
    if (this.isEditing) {
      this.saveProfile();
    } else {
      this.editedUser = { ...this.user };
      this.isEditing = true;
    }
  }

  // Cancel editing and reset temp object
  cancelEdit(): void {
    this.isEditing = false;
    this.editedUser = {};
  }

  // Save profile changes to backend
  saveProfile(): void {
    if (!this.user?.id) return;

    this.userService.updateUser(this.user.id, this.editedUser).subscribe({
      next: (updatedUser) => {
        // Update UI with new user data
        this.user = updatedUser;

        this.isEditing = false;

        // Show success feedback
        this.successMessage = 'Profile updated successfully!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.errorMessage = 'Failed to update profile';
      }
    });
  }

  // Follow this profile user
  followUser(): void {
    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId || !this.user?.id) return;

    this.followService.followUser(currentUserId, this.user.id).subscribe({
      next: (response) => {
        // For private accounts it may return PENDING status
        if (response.status === 'PENDING') {
          this.successMessage = 'Follow request sent! Waiting for approval.';
        } else {
          // For public accounts, follow happens immediately
          this.isFollowing = true;
          this.followersCount++;
          this.successMessage = 'Following user!';
        }
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error following user:', error);
        const errorMsg = error.error?.message || error.message || 'Failed to follow user';
        this.errorMessage = errorMsg;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  // Unfollow this profile user
  unfollowUser(): void {
    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId || !this.user?.id) return;

    this.followService.unfollowUser(currentUserId, this.user.id).subscribe({
      next: () => {
        this.isFollowing = false;
        this.followersCount--;
        this.successMessage = 'Unfollowed user!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error unfollowing user:', error);
        this.errorMessage = 'Failed to unfollow user';
      }
    });
  }

  // Send connection request (LinkedIn-style)
  sendConnectionRequest(): void {
    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId || !this.user?.id) return;

    this.connectionService.sendConnectionRequest(currentUserId, this.user.id).subscribe({
      next: () => {
        this.successMessage = 'Connection request sent!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error sending connection request:', error);
        const errorMsg = error.error?.message || error.message || 'Failed to send connection request';
        this.errorMessage = errorMsg;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }
}