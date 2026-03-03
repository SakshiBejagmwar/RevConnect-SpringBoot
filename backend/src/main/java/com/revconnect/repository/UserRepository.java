package com.revconnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revconnect.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmailOrUsername(String email, String username);

    List<User> findByNameContainingIgnoreCase(String name);
    
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    // Search by username OR name
    List<User> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(String username, String name);
    
    // Find users with public profiles
    List<User> findByAccountPrivacy(String accountPrivacy);

}

