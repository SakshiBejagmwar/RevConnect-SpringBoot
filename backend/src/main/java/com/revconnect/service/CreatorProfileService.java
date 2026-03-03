package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revconnect.entity.CreatorProfile;
import com.revconnect.repository.CreatorProfileRepository;
import java.util.Optional;

@Service
public class CreatorProfileService {

    @Autowired
    private CreatorProfileRepository repository;

    public CreatorProfile createOrUpdateProfile(CreatorProfile profile) {
        return repository.save(profile);
    }

    public Optional<CreatorProfile> getByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public void pinPost(Long userId, Long postId) {
        CreatorProfile profile = repository.findByUserId(userId).orElseThrow();
        profile.setPinnedPostId(postId);
        repository.save(profile);
    }
}
