package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Connection;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findByReceiverIdAndStatus(Long receiverId, String status);

    List<Connection> findBySenderIdAndStatus(Long senderId, String status);

    List<Connection> findBySenderIdOrReceiverIdAndStatus(
            Long senderId, Long receiverId, String status);
    
    // Find specific connection between two users
    Optional<Connection> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);
}
