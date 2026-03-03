/*

6. **analytics.service.ts** - Analytics Service

**Purpose:** Handles post analytics and engagement metrics

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `getPostAnalytics(postId)` | Get post analytics | GET `/api/analytics/post/{postId}` |
| `getEngagementRate(postId)` | Get engagement rate | GET `/api/analytics/post/{postId}/engagement` |

**How It Works:**
```typescript
// Get post analytics
getPostAnalytics(postId) {
  1. Send GET request to backend
  2. Backend calculates:
     - Total likes
     - Total comments
     - Total shares
     - Reach
  3. Return analytics data
  4. Component displays charts/graphs
}
```

**Used By:** AnalyticsComponent, ProfileComponent

**Example Usage:**
```typescript
// In analytics.component.ts
this.analyticsService.getPostAnalytics(postId).subscribe(data => {
  this.totalLikes = data.totalLikes;
  this.totalComments = data.totalComments;
  this.createChart(data);
});
*/



import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PostAnalytics {
  id: number;
  postId: number;
  totalLikes: number;
  totalComments: number;
  totalShares: number;
  reach: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = `${environment.apiUrl}/analytics`;

  constructor(private http: HttpClient) {}

  getPostAnalytics(postId: number): Observable<PostAnalytics> {
    return this.http.get<PostAnalytics>(`${this.apiUrl}/post/${postId}`);
  }

  getEngagementRate(postId: number): Observable<{ engagementRate: number }> {
    return this.http.get<{ engagementRate: number }>(`${this.apiUrl}/post/${postId}/engagement`);
  }
}
