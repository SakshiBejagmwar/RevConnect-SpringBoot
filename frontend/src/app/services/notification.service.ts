/*
5. **notification.service.ts** - Notification Service

**Purpose:** Handles user notifications

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `getMyNotifications()` | Get all notifications | GET `/api/notifications` |
| `getUnreadCount()` | Get unread count | GET `/api/notifications/unread/count` |
| `markAsRead(id)` | Mark notification as read | PUT `/api/notifications/read/{id}` |
| `markAllAsRead()` | Mark all as read | PUT `/api/notifications/read-all` |
| `getPreferences()` | Get notification settings | GET `/api/notifications/preferences` |
| `updatePreferences(preferences)` | Update settings | PUT `/api/notifications/preferences` |

**How It Works:**
```typescript
// Get notifications
getMyNotifications() {
  1. Send GET request to backend
  2. Backend queries notifications for user
  3. Return list of notifications
  4. Component displays in notification panel
}
```

**Used By:** NotificationsComponent, NavbarComponent

**Example Usage:**
```typescript
// In navbar.component.ts
this.notificationService.getUnreadCount().subscribe(data => {
  this.unreadCount = data.count;
  this.showBadge = this.unreadCount > 0;
});

*/


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models/connection.model';
import { environment } from '../../environments/environment';

export interface NotificationPreference {
  id?: number;
  userId?: number;
  likeEnabled: boolean;
  commentEnabled: boolean;
  shareEnabled: boolean;
  followEnabled: boolean;
  followRequestEnabled: boolean;
  connectionRequestEnabled: boolean;
  connectionAcceptedEnabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.apiUrl);
  }

  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/unread/count`);
  }

  markAsRead(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/read/${id}`, {});
  }

  markAllAsRead(): Observable<any> {
    return this.http.put(`${this.apiUrl}/read-all`, {});
  }

  getPreferences(): Observable<NotificationPreference> {
    return this.http.get<NotificationPreference>(`${this.apiUrl}/preferences`);
  }

  updatePreferences(preferences: NotificationPreference): Observable<NotificationPreference> {
    return this.http.put<NotificationPreference>(`${this.apiUrl}/preferences`, preferences);
  }
}
