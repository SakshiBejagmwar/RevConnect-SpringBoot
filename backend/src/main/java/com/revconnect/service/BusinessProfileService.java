package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revconnect.entity.BusinessProfile;
import com.revconnect.repository.BusinessProfileRepository;
import java.util.Optional;

@Service
public class BusinessProfileService {

    @Autowired
    private BusinessProfileRepository repository;

    public BusinessProfile createOrUpdateProfile(BusinessProfile profile) {
        return repository.save(profile);
    }

    public Optional<BusinessProfile> getByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}
