import { Component, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  // Reactive computed value: true/false depending on login status
  isAuthenticated = computed(() => this.authService.isAuthenticated());

  // Reactive computed value: returns currently logged-in user object (if available)
  currentUser = computed(() => this.authService.currentUser());

  // Stores unread notifications count to display in navbar
  unreadCount = 0;

  constructor(
    public authService: AuthService,                 // Public to use directly in HTML template
    private router: Router,                          // Used for navigation (profile redirect)
    private notificationService: NotificationService // Used to fetch unread notification count
  ) {}

  // Runs when navbar component loads
  ngOnInit(): void {
    // Only load notifications if user is logged in
    if (this.isAuthenticated()) {
      this.loadUnreadCount();

      // Refresh unread count every 30 seconds
      // NOTE: In real apps, you should clear this interval in ngOnDestroy
      setInterval(() => this.loadUnreadCount(), 30000);
    }
  }

  // Calls backend to get unread notification count and updates UI badge
  loadUnreadCount(): void {
    this.notificationService.getUnreadCount().subscribe({
      next: (response) => {
        this.unreadCount = response.count;
      },
      error: (error) => console.error('Error loading unread count:', error)
    });
  }

  // Logs user out (usually clears token + user data)
  logout(): void {
    this.authService.logout();
  }

  // Navigates user to their profile page using current userId
  navigateToProfile(): void {
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.router.navigate(['/profile', userId]);
    }
  }
}