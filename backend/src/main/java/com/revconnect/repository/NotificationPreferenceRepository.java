package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.NotificationPreference;
import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    
    Optional<NotificationPreference> findByUserId(Long userId);
}
