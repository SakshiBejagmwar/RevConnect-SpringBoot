export interface Post {
  id?: number;
  content: string;
  authorId: number;
  createdAt?: string;
  originalPostId?: number;
  isShared?: boolean;
  authorName?: string;
  likeCount?: number;
  commentCount?: number;
  isLikedByCurrentUser?: boolean;
  postType?: string;
  callToAction?: string;
  mediaPath?: string;
  isPinned?: boolean;
}

export interface Comment {
  id?: number;
  content: string;
  userId: number;
  postId: number;
  createdAt?: string;
  userName?: string;
}

export interface Like {
  userId: number;
  postId: number;
}
