package com.revconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revconnect.entity.Hashtag;
import com.revconnect.entity.Post;
import com.revconnect.repository.HashtagRepository;
import com.revconnect.repository.PostRepository;

import java.util.regex.*;
import java.util.*;

@Service
public class HashtagService {

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostRepository postRepository;

    public void processHashtags(String content, Post post) {
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String tagName = matcher.group(1).toLowerCase();

            Hashtag hashtag = hashtagRepository
                    .findByTag(tagName)
                    .orElseGet(() -> {
                        Hashtag newTag = new Hashtag();
                        newTag.setTag(tagName);
                        return hashtagRepository.save(newTag);
                    });

            hashtag.setUsageCount(hashtag.getUsageCount() + 1);
            post.getHashtags().add(hashtag);
            hashtagRepository.save(hashtag);
        }
    }

    public List<Hashtag> getTrendingHashtags() {
        return hashtagRepository.findTop10ByOrderByUsageCountDesc();
    }
}
