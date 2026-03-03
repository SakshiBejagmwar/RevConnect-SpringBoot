package com.revconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revconnect.entity.PostHashtag;
import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    List<PostHashtag> findByHashtagId(Long hashtagId);
}
