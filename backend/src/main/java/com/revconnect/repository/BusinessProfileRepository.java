package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.BusinessProfile;
import java.util.Optional;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {

    Optional<BusinessProfile> findByUserId(Long userId);
}
