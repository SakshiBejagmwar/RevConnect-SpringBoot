package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revconnect.repository.ConnectionRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // ✅ SEND CONNECTION REQUEST
    @Transactional
    public Connection sendRequest(Long senderId, Long receiverId) {
        // Validation: Prevent self-connection
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot send connection request to yourself");
        }

        // Validation: Check if connection already exists
        Optional<Connection> existing1 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "PENDING");
        Optional<Connection> existing2 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(receiverId, senderId, "PENDING");
        Optional<Connection> existing3 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "ACCEPTED");
        Optional<Connection> existing4 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(receiverId, senderId, "ACCEPTED");

        if (existing1.isPresent() || existing2.isPresent()) {
            throw new RuntimeException("Connection request already sent");
        }
        if (existing3.isPresent() || existing4.isPresent()) {
            throw new RuntimeException("Already connected");
        }

        // Validate receiver exists
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create connection
        Connection connection = new Connection();
        connection.setSenderId(senderId);
        connection.setReceiverId(receiverId);
        connection.setStatus("PENDING");
        
        Connection saved = connectionRepository.save(connection);

        // Send notification
        notificationService.createNotification(
                senderId,
                receiverId,
                "CONNECTION_REQUEST",
                null,
                "Someone sent you a connection request"
        );

        return saved;
    }

    // ✅ ACCEPT CONNECTION REQUEST
    @Transactional
    public Connection acceptRequest(Long connectionId, Long requestingUserId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        // Authorization: Only receiver can accept
        if (!connection.getReceiverId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only accept requests sent to you");
        }

        if (!"PENDING".equals(connection.getStatus())) {
            throw new RuntimeException("Connection request is not pending");
        }

        connection.setStatus("ACCEPTED");
        Connection updated = connectionRepository.save(connection);

        // Notify sender that request was accepted
        notificationService.createNotification(
                connection.getReceiverId(),
                connection.getSenderId(),
                "CONNECTION_ACCEPTED",
                null,
                "Your connection request was accepted"
        );

        return updated;
    }

    // ✅ REJECT CONNECTION REQUEST
    @Transactional
    public void rejectRequest(Long connectionId, Long requestingUserId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        // Authorization: Only receiver can reject
        if (!connection.getReceiverId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only reject requests sent to you");
        }

        connectionRepository.delete(connection);
    }

    // ✅ GET PENDING REQUESTS RECEIVED
    public List<Connection> getPendingRequestsReceived(Long userId) {
        return connectionRepository.findByReceiverIdAndStatus(userId, "PENDING");
    }

    // ✅ GET PENDING REQUESTS SENT
    public List<Connection> getPendingRequestsSent(Long userId) {
        return connectionRepository.findBySenderIdAndStatus(userId, "PENDING");
    }

    // ✅ GET ALL CONNECTIONS
    public List<Connection> getConnections(Long userId) {
        return connectionRepository
                .findBySenderIdOrReceiverIdAndStatus(userId, userId, "ACCEPTED");
    }

    // ✅ REMOVE CONNECTION
    @Transactional
    public void removeConnection(Long connectionId, Long requestingUserId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        // Authorization: Only participants can remove
        if (!connection.getSenderId().equals(requestingUserId) && 
            !connection.getReceiverId().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You can only remove your own connections");
        }

        connectionRepository.delete(connection);
    }

    // ✅ CHECK CONNECTION STATUS
    public String getConnectionStatus(Long userId1, Long userId2) {
        Optional<Connection> conn1 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(userId1, userId2, "ACCEPTED");
        Optional<Connection> conn2 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(userId2, userId1, "ACCEPTED");

        if (conn1.isPresent() || conn2.isPresent()) {
            return "CONNECTED";
        }

        Optional<Connection> pending1 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(userId1, userId2, "PENDING");
        Optional<Connection> pending2 = connectionRepository
                .findBySenderIdAndReceiverIdAndStatus(userId2, userId1, "PENDING");

        if (pending1.isPresent()) {
            return "REQUEST_SENT";
        }
        if (pending2.isPresent()) {
            return "REQUEST_RECEIVED";
        }

        return "NOT_CONNECTED";
    }
}
