package com.revconnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.revconnect.service.HashtagService;
import com.revconnect.repository.PostRepository;
import com.revconnect.entity.Hashtag;
import com.revconnect.entity.Post;

import java.util.*;

@RestController
@RequestMapping("/api/hashtags")
public class HashtagController {

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private PostRepository postRepository;

    // Get trending hashtags
    @GetMapping("/trending")
    public List<Hashtag> getTrendingHashtags() {
        return hashtagService.getTrendingHashtags();
    }

    // Search posts by hashtag
    @GetMapping("/search/{tag}")
    public List<Post> searchByHashtag(@PathVariable String tag) {
        // Find posts that contain this hashtag
        List<Post> posts = postRepository.findByHashtag(tag);
        return posts;
    }
}
