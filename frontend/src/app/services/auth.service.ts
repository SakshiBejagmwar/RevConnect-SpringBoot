/*
Purpose:** Handles user login, registration, and authentication state

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `register(user)` | Register new user | POST `/api/auth/register` |
| `login(credentials)` | User login | POST `/api/auth/login` |
| `logout()` | Logout user, clear token | None (local only) |
| `getToken()` | Get JWT token from localStorage | None |
| `getCurrentUserId()` | Get logged-in user's ID | None |
| `getCurrentUser()` | Get logged-in user details | None |

**How It Works:**
```typescript
// User logs in
login(credentials) {
  1. Send POST request to backend
  2. Receive JWT token and user data
  3. Store token in localStorage
  4. Store user data in localStorage
  5. Update authentication state
  6. Return response to component
}
```

**Used By:** LoginComponent, RegisterComponent, NavbarComponent

**Example Usage:**
```typescript
// In login.component.ts
this.authService.login({ email, password }).subscribe(
  response => {
    // Login successful, navigate to feed
    this.router.navigate(['/feed']);
  }
);
*/


import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { User, LoginRequest, AuthResponse } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  currentUser = signal<User | null>(null);
  isAuthenticated = signal<boolean>(false);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  register(user: User): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, user).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => this.handleAuthResponse(response))
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCurrentUserId(): number | null {
    const user = this.currentUser();
    return user?.id || null;
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  private handleAuthResponse(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('user', JSON.stringify(response.user));
    this.currentUser.set(response.user);
    this.isAuthenticated.set(true);
  }

  private loadUserFromStorage(): void {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
      } catch (error) {
        this.logout();
      }
    }
  }
}
