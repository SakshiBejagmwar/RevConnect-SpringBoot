package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.CreatorProfile;
import java.util.Optional;

public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, Long> {

    Optional<CreatorProfile> findByUserId(Long userId);
}
