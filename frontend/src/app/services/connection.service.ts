/*
 4. **connection.service.ts** - Connection Management Service

**Purpose:** Handles connection requests between users

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `sendConnectionRequest(senderId, receiverId)` | Send connection request | POST `/api/connections/send/{receiverId}` |
| `acceptConnection(id)` | Accept connection | PUT `/api/connections/accept/{id}` |
| `rejectConnection(id)` | Reject connection | DELETE `/api/connections/reject/{id}` |
| `getPendingConnections(userId)` | Get received requests | GET `/api/connections/pending/received` |
| `getPendingSentConnections(userId)` | Get sent requests | GET `/api/connections/pending/sent` |
| `getMyConnections()` | Get all connections | GET `/api/connections/my-connections` |
| `removeConnection(id)` | Remove connection | DELETE `/api/connections/{id}` |
| `getConnectionStatus(otherUserId)` | Check connection status | GET `/api/connections/status/{otherUserId}` |

**How It Works:**
```typescript
// Send connection request
sendConnectionRequest(senderId, receiverId) {
  1. Send POST request to backend
  2. Backend creates connection with status "PENDING"
  3. Backend sends notification to receiver
  4. Return connection object
  5. Component shows "Request Sent"
}
```

**Used By:** ConnectionsComponent, ProfileComponent

**Example Usage:**
```typescript
// In profile.component.ts
this.connectionService.sendConnectionRequest(myId, userId).subscribe(() => {
  this.connectionStatus = 'PENDING';
  alert('Connection request sent!');
});

*/


import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Connection } from '../models/connection.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {
  private apiUrl = `${environment.apiUrl}/connections`;

  constructor(private http: HttpClient) {}

  sendConnectionRequest(senderId: number, receiverId: number): Observable<Connection> {
    return this.http.post<Connection>(`${this.apiUrl}/send/${receiverId}`, {});
  }

  acceptConnection(id: number): Observable<Connection> {
    return this.http.put<Connection>(`${this.apiUrl}/accept/${id}`, {});
  }

  rejectConnection(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/reject/${id}`);
  }

  getPendingConnections(userId: number): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/pending/received`);
  }

  getPendingSentConnections(userId: number): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/pending/sent`);
  }

  getMyConnections(): Observable<Connection[]> {
    return this.http.get<Connection[]>(`${this.apiUrl}/my-connections`);
  }

  removeConnection(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getConnectionStatus(otherUserId: number): Observable<{status: string}> {
    return this.http.get<{status: string}>(`${this.apiUrl}/status/${otherUserId}`);
  }
}
