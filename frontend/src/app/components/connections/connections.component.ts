/*
This component manages:
🔗 Connections (Linked-style)
👥 Followers / Following (Instagram-style)
📩 Pending requests
✅ Accept / Reject actions
❌ Remove / Unfollow actions
📊 Tab-based UI management
*/





import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ConnectionService } from '../../services/connection.service';
import { FollowService, Follow } from '../../services/follow.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Connection } from '../../models/connection.model';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-connections',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {

  // Stores pending connection requests
  pendingConnections: Connection[] = [];

  // Stores accepted connections
  myConnections: Connection[] = [];

  // Stores user details of connections (used for UI display)
  myConnectionUsers: User[] = [];

  // Pending follow requests (private accounts)
  pendingFollowRequests: Follow[] = [];

  // Followers list
  followers: Follow[] = [];

  // Following list
  following: Follow[] = [];

  // Detailed user data for followers and following
  followerUsers: User[] = [];
  followingUsers: User[] = [];

  // Controls which tab is active in UI
  activeTab: 'pending-connections' | 'pending-follows' | 'my-connections' | 'followers' | 'following' = 'my-connections';

  successMessage = '';
  errorMessage = '';

  constructor(
    private connectionService: ConnectionService,
    private followService: FollowService,
    private authService: AuthService,
    private userService: UserService
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    this.loadData();
  }

  // Loads all connection and follow-related data
  loadData(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.loadPendingConnections(userId);
    this.loadMyConnections();
    this.loadPendingFollowRequests(userId);
    this.loadFollowers(userId);
    this.loadFollowing(userId);
  }

  // Load pending connection requests
  loadPendingConnections(userId: number): void {
    this.connectionService.getPendingConnections(userId).subscribe({
      next: (connections) => this.pendingConnections = connections,
      error: (error) => console.error('Error loading connections:', error)
    });
  }

  // Load accepted connections
  loadMyConnections(): void {
    this.connectionService.getMyConnections().subscribe({
      next: (connections) => {
        this.myConnections = connections;
        this.myConnectionUsers = [];

        const currentUserId = this.authService.getCurrentUserId();

        // Get the other user's details in each connection
        connections.forEach(connection => {
          const otherUserId =
            connection.senderId === currentUserId
              ? connection.receiverId
              : connection.senderId;

          this.userService.getUserById(otherUserId).subscribe({
            next: (user) => this.myConnectionUsers.push(user),
            error: (error) => console.error('Error loading connection user:', error)
          });
        });
      },
      error: (error) => console.error('Error loading my connections:', error)
    });
  }

  // Load pending follow requests
  loadPendingFollowRequests(userId: number): void {
    this.followService.getPendingRequests(userId).subscribe({
      next: (requests) => {
        this.pendingFollowRequests = requests;

        // Load follower name for display
        requests.forEach(request => {
          this.userService.getUserById(request.followerId).subscribe({
            next: (user) => (request as any).followerName = user.name
          });
        });
      },
      error: (error) => console.error('Error loading follow requests:', error)
    });
  }

  // Load followers
  loadFollowers(userId: number): void {
    this.followService.getFollowers(userId).subscribe({
      next: (follows) => {
        this.followers = follows;
        this.followerUsers = [];

        follows.forEach(follow => {
          this.userService.getUserById(follow.followerId).subscribe({
            next: (user) => this.followerUsers.push(user),
            error: (error) => console.error('Error loading follower user:', error)
          });
        });
      },
      error: (error) => console.error('Error loading followers:', error)
    });
  }

  // Load users the current user is following
  loadFollowing(userId: number): void {
    this.followService.getFollowing(userId).subscribe({
      next: (follows) => {
        this.following = follows;
        this.followingUsers = [];

        follows.forEach(follow => {
          this.userService.getUserById(follow.followingId).subscribe({
            next: (user) => this.followingUsers.push(user),
            error: (error) => console.error('Error loading following user:', error)
          });
        });
      },
      error: (error) => console.error('Error loading following:', error)
    });
  }

  // Accept a connection request
  acceptConnection(connectionId: number | undefined): void {
    if (!connectionId) return;

    this.connectionService.acceptConnection(connectionId).subscribe({
      next: () => {
        this.pendingConnections =
          this.pendingConnections.filter(c => c.id !== connectionId);
        this.successMessage = 'Connection accepted!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => this.errorMessage = 'Failed to accept connection'
    });
  }

  // Reject a connection request
  rejectConnection(connectionId: number | undefined): void {
    if (!connectionId) return;

    this.connectionService.rejectConnection(connectionId).subscribe({
      next: () => {
        this.pendingConnections =
          this.pendingConnections.filter(c => c.id !== connectionId);
        this.successMessage = 'Connection rejected!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => this.errorMessage = 'Failed to reject connection'
    });
  }

  // Accept a follow request
  acceptFollowRequest(followId: number | undefined): void {
    if (!followId) return;

    this.followService.acceptFollowRequest(followId).subscribe({
      next: () => {
        this.pendingFollowRequests =
          this.pendingFollowRequests.filter(f => f.id !== followId);

        this.successMessage = 'Follow request accepted!';
        setTimeout(() => this.successMessage = '', 3000);

        const userId = this.authService.getCurrentUserId();
        if (userId) this.loadFollowers(userId);
      },
      error: () => this.errorMessage = 'Failed to accept follow request'
    });
  }

  // Reject a follow request
  rejectFollowRequest(followId: number | undefined): void {
    if (!followId) return;

    this.followService.rejectFollowRequest(followId).subscribe({
      next: () => {
        this.pendingFollowRequests =
          this.pendingFollowRequests.filter(f => f.id !== followId);
        this.successMessage = 'Follow request rejected!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => this.errorMessage = 'Failed to reject follow request'
    });
  }

  // Unfollow a user
  unfollowUser(followingId: number | undefined): void {
    if (!followingId) return;

    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId) return;

    this.followService.unfollowUser(currentUserId, followingId).subscribe({
      next: () => {
        this.followingUsers =
          this.followingUsers.filter(u => u.id !== followingId);

        this.following =
          this.following.filter(f => f.followingId !== followingId);

        this.successMessage = 'Unfollowed user!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => this.errorMessage = 'Failed to unfollow user'
    });
  }

  // Remove an existing connection
  removeConnection(userId: number | undefined): void {
    if (!userId) return;

    const currentUserId = this.authService.getCurrentUserId();
    if (!currentUserId) return;

    const connection = this.myConnections.find(c =>
      (c.senderId === currentUserId && c.receiverId === userId) ||
      (c.receiverId === currentUserId && c.senderId === userId)
    );

    if (!connection || !connection.id) return;

    if (confirm('Are you sure you want to remove this connection?')) {
      this.connectionService.removeConnection(connection.id).subscribe({
        next: () => {
          this.myConnectionUsers =
            this.myConnectionUsers.filter(u => u.id !== userId);

          this.myConnections =
            this.myConnections.filter(c => c.id !== connection.id);

          this.successMessage = 'Connection removed!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => this.errorMessage = 'Failed to remove connection'
      });
    }
  }

  // Switch between UI tabs
  setActiveTab(tab: 'pending-connections' | 'pending-follows' | 'my-connections' | 'followers' | 'following'): void {
    this.activeTab = tab;
  }

  // Get follower name safely
  getFollowerName(request: Follow): string {
    return (request as any).followerName || 'User';
  }
}