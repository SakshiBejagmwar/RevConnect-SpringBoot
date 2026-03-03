import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { PostService } from '../../services/post.service';
import { User } from '../../models/user.model';
import { Post } from '../../models/post.model';
import { PostCardComponent } from '../post-card/post-card.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PostCardComponent],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {
  // Text typed by user in search input
  searchQuery = '';

  // Controls what user wants to search: users / posts / hashtags
  searchType: 'users' | 'posts' | 'hashtags' = 'users';

  // Search results
  users: User[] = [];
  posts: Post[] = [];

  // UI state flags
  isLoading = false;
  hasSearched = false; // Used to show "no results" message after first search

  constructor(
    private userService: UserService, // API calls for user search
    private postService: PostService  // API calls for post/hashtag search
  ) {}

  // Called when user clicks search button / presses enter
  search(): void {
    // Do nothing if search input is empty
    if (!this.searchQuery.trim()) return;

    // Start loading + reset previous results
    this.isLoading = true;
    this.hasSearched = true;
    this.users = [];
    this.posts = [];

    // Call correct search API based on selected search type
    switch (this.searchType) {
      case 'users':
        this.searchUsers();
        break;
      case 'posts':
        this.searchPosts();
        break;
      case 'hashtags':
        this.searchHashtags();
        break;
    }
  }

  // Searches users by name/username/email etc. (depends on backend)
  searchUsers(): void {
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: (users) => {
        this.users = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching users:', error);
        this.isLoading = false;
      }
    });
  }

  // Searches posts by keyword/content etc. (depends on backend)
  searchPosts(): void {
    this.postService.searchPosts(this.searchQuery).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching posts:', error);
        this.isLoading = false;
      }
    });
  }

  // Searches posts by hashtag (removes leading # if user typed it)
  searchHashtags(): void {
    const tag = this.searchQuery.startsWith('#')
      ? this.searchQuery.substring(1)
      : this.searchQuery;

    this.postService.searchByHashtag(tag).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching hashtags:', error);
        this.isLoading = false;
      }
    });
  }

  // Called by PostCardComponent when a post is deleted (to remove it from results)
  onPostDeleted(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
  }
}