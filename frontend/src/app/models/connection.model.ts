export interface Connection {
  id?: number;
  senderId: number;
  receiverId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt?: string;
  senderName?: string;
  receiverName?: string;
}

export interface Follow {
  followerId: number;
  followingId: number;
  createdAt?: string;
}

export interface Notification {
  id?: number;
  userId: number;
  type: string;
  message: string;
  isRead: boolean;
  createdAt?: string;
}
