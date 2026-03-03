package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    
    Long countByReceiverIdAndIsRead(Long receiverId, boolean isRead);
    
    List<Notification> findByReceiverIdAndIsRead(Long receiverId, boolean isRead);
}
