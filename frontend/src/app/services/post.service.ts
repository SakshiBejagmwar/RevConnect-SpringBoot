/*
2. **post.service.ts** - Post Management Service

**Purpose:** Handles all post-related operations (create, read, update, delete, like, comment)

**Key Functions:**

| Function | What It Does | Backend API Called |
|----------|-------------|-------------------|
| `createPost(post)` | Create new post | POST `/api/posts` |
| `getAllPosts()` | Get all posts | GET `/api/posts` |
| `getPostById(id)` | Get single post | GET `/api/posts/{id}` |
| `getFeed(userId)` | Get user's feed | GET `/api/posts/feed` |
| `updatePost(id, post)` | Update post | PUT `/api/posts/{id}` |
| `deletePost(id)` | Delete post | DELETE `/api/posts/{id}` |
| `addComment(comment)` | Add comment to post | POST `/api/comments` |
| `getCommentsByPost(postId)` | Get post comments | GET `/api/comments/post/{postId}` |
| `likePost(like)` | Like a post | POST `/api/likes/{postId}` |
| `unlikePost(userId, postId)` | Unlike a post | DELETE `/api/likes/{postId}` |
| `getLikeCount(postId)` | Get like count | GET `/api/likes/count/{postId}` |
| `checkIfLiked(postId)` | Check if user liked | GET `/api/likes/check/{postId}` |
| `pinPost(postId)` | Pin post to profile | PUT `/api/posts/pin/{postId}` |
| `unpinPost(postId)` | Unpin post | PUT `/api/posts/unpin/{postId}` |
| `getTrendingPosts()` | Get trending posts | GET `/api/posts/trending` |
| `sharePost(postId)` | Share/repost | POST `/api/posts/share/{postId}` |
| `uploadFile(file)` | Upload media file | POST `/api/files/upload` |
| `deleteFile(filePath)` | Delete uploaded file | DELETE `/api/files/delete` |
| `searchPosts(keyword)` | Search posts | GET `/api/search/posts` |
| `searchByHashtag(tag)` | Search by hashtag | GET `/api/search/hashtag` |

**How It Works:**
```typescript
// Create a post with image
1. User selects image file
2. Call uploadFile(file) → uploads to backend
3. Get file path from response
4. Call createPost({ content, mediaPath }) → creates post
5. Backend saves post with media path
6. Return created post to component
7. Component displays new post in feed
```

**Used By:** FeedComponent, PostCardComponent, ProfileComponent

**Example Usage:**
```typescript
// In feed.component.ts
// Upload file first
this.postService.uploadFile(file).subscribe(response => {
  const mediaPath = response.filePath;
  
  // Then create post
  this.postService.createPost({
    content: 'My new post',
    mediaPath: mediaPath
  }).subscribe(post => {
    this.posts.unshift(post); // Add to feed
  });
});

*/
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Post, Comment, Like } from '../models/post.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  createPost(post: Post): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  getFeed(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed`);
  }
  
  updatePost(id: number, post: Post): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, post);
  }

  deletePost(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  // Comments
  addComment(comment: Comment): Observable<Comment> {
    return this.http.post<Comment>(`${environment.apiUrl}/comments`, comment);
  }

  getCommentsByPost(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${environment.apiUrl}/comments/post/${postId}`);
  }
  
  getCommentCount(postId: number): Observable<number> {
    return this.http.get<{count: number}>(`${environment.apiUrl}/comments/count/${postId}`)
      .pipe(map(response => response.count));
  }

  deleteComment(id: number): Observable<string> {
    return this.http.delete<string>(`${environment.apiUrl}/comments/${id}`);
  }

  // Likes
  likePost(like: Like): Observable<any> {
    return this.http.post(`${environment.apiUrl}/likes/${like.postId}`, {});
  }

  unlikePost(userId: number, postId: number): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/likes/${postId}`);
  }

  getLikeCount(postId: number): Observable<number> {
    return this.http.get<{count: number}>(`${environment.apiUrl}/likes/count/${postId}`)
      .pipe(map(response => response.count));
  }

  checkIfLiked(postId: number): Observable<boolean> {
    return this.http.get<{liked: boolean}>(`${environment.apiUrl}/likes/check/${postId}`)
      .pipe(map(response => response.liked));
  }

  searchPosts(keyword: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${environment.apiUrl}/search/posts`, {
      params: { keyword }
    });
  }

  searchByHashtag(tag: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${environment.apiUrl}/search/hashtag`, {
      params: { tag }
    });
  }

  // Advanced features
  pinPost(postId: number): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/pin/${postId}`, {});
  }

  unpinPost(postId: number): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/unpin/${postId}`, {});
  }

  getTrendingPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/trending`);
  }

  getFeedByPostType(postType: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed/filter/type`, {
      params: { postType }
    });
  }

  getFeedByUserType(userType: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed/filter/user-type`, {
      params: { userType }
    });
  }

  sharePost(postId: number): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/share/${postId}`, {});
  }

  // File upload
  uploadFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${environment.apiUrl}/files/upload`, formData);
  }

  deleteFile(filePath: string): Observable<any> {
    return this.http.delete(`${environment.apiUrl}/files/delete`, {
      params: { filePath }
    });
  }
}
