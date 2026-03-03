package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.Hashtag;
import java.util.Optional;
import java.util.List;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByTag(String tag);

    List<Hashtag> findTop10ByOrderByUsageCountDesc();
}
