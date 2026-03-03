import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificationService, NotificationPreference } from '../../services/notification.service';
import { AuthService } from '../../services/auth.service';
import { Notification } from '../../models/connection.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  // List of notifications fetched from backend
  notifications: Notification[] = [];

  // Unread notification count (used for badge + tab)
  unreadCount = 0;

  // Loading flag for showing spinner/loading UI
  isLoading = false;

  // Toggle to show/hide notification preferences form
  showPreferences = false;

  // User notification preferences (default values)
  preferences: NotificationPreference = {
    likeEnabled: true,
    commentEnabled: true,
    shareEnabled: true,
    followEnabled: true,
    followRequestEnabled: true,
    connectionRequestEnabled: true,
    connectionAcceptedEnabled: true
  };

  // Tab control: show all notifications OR only unread
  activeTab: 'all' | 'unread' = 'all';

  constructor(
    private notificationService: NotificationService, // API calls for notifications
    private authService: AuthService                  // Auth context (not used directly here, but available)
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    this.loadNotifications(); // fetch notifications list
    this.loadUnreadCount();   // fetch unread count
    this.loadPreferences();   // fetch user notification preferences
  }

  // Fetch all notifications for the logged-in user
  loadNotifications(): void {
    this.isLoading = true;

    this.notificationService.getMyNotifications().subscribe({
      next: (notifications: any) => {
        this.notifications = notifications;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading notifications:', error);
        this.isLoading = false;
      }
    });
  }

  // Fetch unread notification count (used for badge)
  loadUnreadCount(): void {
    this.notificationService.getUnreadCount().subscribe({
      next: (response) => {
        this.unreadCount = response.count;
      },
      error: (error) => console.error('Error loading unread count:', error)
    });
  }

  // Fetch saved notification preferences from backend
  loadPreferences(): void {
    this.notificationService.getPreferences().subscribe({
      next: (prefs) => {
        this.preferences = prefs;
      },
      error: (error) => console.error('Error loading preferences:', error)
    });
  }

  /**
   * Returns notifications list depending on selected tab.
   * - "unread" tab => only notifications where isRead = false
   * - "all" tab => returns full list
   */
  get filteredNotifications(): Notification[] {
    if (this.activeTab === 'unread') {
      return this.notifications.filter(n => !n.isRead);
    }
    return this.notifications;
  }

  // Mark a single notification as read
  markAsRead(notification: Notification): void {
    // Skip if already read or id missing
    if (!notification.id || notification.isRead) return;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        // Update UI immediately without reloading
        notification.isRead = true;

        // Reduce unread count safely
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      },
      error: (error) => console.error('Error marking notification as read:', error)
    });
  }

  // Mark all notifications as read at once
  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        // Update UI locally
        this.notifications.forEach(n => n.isRead = true);
        this.unreadCount = 0;
      },
      error: (error) => console.error('Error marking all as read:', error)
    });
  }

  // Show/hide preferences section
  togglePreferences(): void {
    this.showPreferences = !this.showPreferences;
  }

  // Save updated preferences to backend
  savePreferences(): void {
    this.notificationService.updatePreferences(this.preferences).subscribe({
      next: () => {
        alert('Notification preferences saved!');
        this.showPreferences = false;
      },
      error: (error) => {
        console.error('Error saving preferences:', error);
        alert('Failed to save preferences');
      }
    });
  }

  // Switch between "all" and "unread" tabs
  setActiveTab(tab: 'all' | 'unread'): void {
    this.activeTab = tab;
  }

  // Returns emoji/icon based on notification type for UI
  getNotificationIcon(type: string): string {
    switch (type) {
      case 'CONNECTION_REQUEST':
        return '👥';
      case 'CONNECTION_ACCEPTED':
        return '✅';
      case 'FOLLOW':
      case 'NEW_FOLLOWER':
        return '👤';
      case 'FOLLOW_REQUEST':
        return '🔔';
      case 'FOLLOW_ACCEPTED':
        return '✅';
      case 'LIKE':
        return '❤️';
      case 'COMMENT':
        return '💬';
      case 'SHARE':
        return '🔄';
      default:
        return '🔔';
    }
  }

  /**
   * Formats notification date as:
   * - Just now
   * - Xm ago
   * - Xh ago
   * - Xd ago
   * - else => local date string
   */
  formatDate(date: string | undefined): string {
    if (!date) return '';

    const notifDate = new Date(date);
    const now = new Date();

    const diffMs = now.getTime() - notifDate.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;

    return notifDate.toLocaleDateString();
  }
}