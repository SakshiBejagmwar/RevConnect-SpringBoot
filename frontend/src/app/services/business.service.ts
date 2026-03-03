/*8. **business.service.ts** - Business Profile Service

**Purpose:** Handles business profile operations

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `createProfile(profile)` | Create business profile | POST `/api/business/profile` |
| `getMyProfile()` | Get my business profile | GET `/api/business/profile` |
| `getProfileByUserId(userId)` | Get business profile | GET `/api/business/profile/{userId}` |
| `updateProfile(profile)` | Update business profile | PUT `/api/business/profile` |

**Used By:** ProfileSetupComponent, ProfileComponent
*/


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface BusinessProfile {
  id?: number;
  userId?: number;
  businessName: string;
  industry: string;
  detailedBio: string;
  contactInfo: string;
  website: string;
  socialMediaLinks: string;
  businessAddress: string;
  businessHours: string;
  externalLinks: string;
  productsDescription: string;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessService {
  private apiUrl = `${environment.apiUrl}/business`;

  constructor(private http: HttpClient) {}

  createOrUpdateProfile(profile: BusinessProfile): Observable<BusinessProfile> {
    return this.http.post<BusinessProfile>(`${this.apiUrl}/profile`, profile);
  }

  getMyProfile(): Observable<BusinessProfile> {
    return this.http.get<BusinessProfile>(`${this.apiUrl}/profile`);
  }

  getProfileByUserId(userId: number): Observable<BusinessProfile> {
    return this.http.get<BusinessProfile>(`${this.apiUrl}/profile/${userId}`);
  }
}
