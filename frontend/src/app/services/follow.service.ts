/*
7. **follow.service.ts** - Follow System Service

**Purpose:** Handles follow/unfollow operations

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `followUser(followingId)` | Follow a user | POST `/api/follow/{followingId}` |
| `unfollowUser(followingId)` | Unfollow a user | DELETE `/api/follow/{followingId}` |
| `getFollowers()` | Get my followers | GET `/api/follow/followers` |
| `getFollowing()` | Get who I follow | GET `/api/follow/following` |
| `getFollowerCount()` | Get follower count | GET `/api/follow/count/followers` |
| `getFollowingCount()` | Get following count | GET `/api/follow/count/following` |

**Used By:** ProfileComponent, ConnectionsComponent

*/


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

export interface Follow {
  id: number;
  followerId: number;
  followingId: number;
  status: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FollowService {
  private apiUrl = `${environment.apiUrl}/follow`;

  constructor(private http: HttpClient) {}

  followUser(followerId: number, followingId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${followingId}`, {});
  }

  unfollowUser(followerId: number, followingId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${followingId}`);
  }

  getFollowing(userId: number): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.apiUrl}/following`);
  }

  getFollowers(userId: number): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.apiUrl}/followers`);
  }
  
  checkFollowStatus(followerId: number, followingId: number): Observable<{isFollowing: boolean}> {
    return this.http.get<{isFollowing: boolean}>(`${this.apiUrl}/status/${followingId}`);
  }
  
  getPendingRequests(userId: number): Observable<Follow[]> {
    return this.http.get<Follow[]>(`${this.apiUrl}/requests`);
  }
  
  acceptFollowRequest(followId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/accept/${followId}`, {});
  }
  
  rejectFollowRequest(followId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/reject/${followId}`);
  }
}
