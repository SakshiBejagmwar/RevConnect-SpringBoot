/*
9. **creator.service.ts** - Creator Profile Service

**Purpose:** Handles creator profile operations

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `createProfile(profile)` | Create creator profile | POST `/api/creator/profile` |
| `getMyProfile()` | Get my creator profile | GET `/api/creator/profile` |
| `getProfileByUserId(userId)` | Get creator profile | GET `/api/creator/profile/{userId}` |
| `updateProfile(profile)` | Update creator profile | PUT `/api/creator/profile` |

**Used By:** ProfileSetupComponent, ProfileComponent
*/


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreatorProfile {
  id?: number;
  userId?: number;
  creatorName: string;
  category: string;
  detailedBio: string;
  contactInfo: string;
  website: string;
  socialMediaLinks: string;
  externalLinks: string;
  pinnedPostId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class CreatorService {
  private apiUrl = `${environment.apiUrl}/creator`;

  constructor(private http: HttpClient) {}

  createOrUpdateProfile(profile: CreatorProfile): Observable<CreatorProfile> {
    return this.http.post<CreatorProfile>(`${this.apiUrl}/profile`, profile);
  }

  getMyProfile(): Observable<CreatorProfile> {
    return this.http.get<CreatorProfile>(`${this.apiUrl}/profile`);
  }

  getProfileByUserId(userId: number): Observable<CreatorProfile> {
    return this.http.get<CreatorProfile>(`${this.apiUrl}/profile/${userId}`);
  }
}
