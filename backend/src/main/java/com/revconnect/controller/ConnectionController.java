package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.ConnectionService;
import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserRepository userRepository;

    // Send connection request
    @PostMapping("/send/{receiverId}")
    public ResponseEntity<?> sendRequest(@PathVariable Long receiverId, 
                                         Authentication authentication) {
        try {
            Long senderId = getUserIdFromAuth(authentication);
            Connection connection = connectionService.sendRequest(senderId, receiverId);
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Accept connection request
    @PutMapping("/accept/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id, 
                                           Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Connection connection = connectionService.acceptRequest(id, userId);
            return ResponseEntity.ok(connection);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Reject connection request
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, 
                                           Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            connectionService.rejectRequest(id, userId);
            return ResponseEntity.ok(Map.of("message", "Connection request rejected"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Get pending requests received
    @GetMapping("/pending/received")
    public ResponseEntity<List<Connection>> getPendingRequestsReceived(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Connection> requests = connectionService.getPendingRequestsReceived(userId);
        return ResponseEntity.ok(requests);
    }

    // Get pending requests sent
    @GetMapping("/pending/sent")
    public ResponseEntity<List<Connection>> getPendingRequestsSent(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Connection> requests = connectionService.getPendingRequestsSent(userId);
        return ResponseEntity.ok(requests);
    }

    // Get my connections
    @GetMapping("/my-connections")
    public ResponseEntity<List<Connection>> getMyConnections(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Connection> connections = connectionService.getConnections(userId);
        return ResponseEntity.ok(connections);
    }

    // Get connections for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Connection>> getUserConnections(@PathVariable Long userId) {
        List<Connection> connections = connectionService.getConnections(userId);
        return ResponseEntity.ok(connections);
    }

    // Remove connection
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeConnection(@PathVariable Long id, 
                                               Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            connectionService.removeConnection(id, userId);
            return ResponseEntity.ok(Map.of("message", "Connection removed successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Check connection status with another user
    @GetMapping("/status/{otherUserId}")
    public ResponseEntity<Map<String, String>> getConnectionStatus(@PathVariable Long otherUserId,
                                                                    Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        String status = connectionService.getConnectionStatus(userId, otherUserId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        
        String email = authentication.getPrincipal().toString();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        return userOpt.get().getId();
    }
}
