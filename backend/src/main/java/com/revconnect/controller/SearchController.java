package com.revconnect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/posts")
    public List<Post> searchPosts(@RequestParam String keyword) {
        return searchService.searchPosts(keyword);
    }

    @GetMapping("/users")
    public List<User> searchUsers(@RequestParam String query) {
        return searchService.searchUsers(query);
    }

    @GetMapping("/hashtag")
    public List<Post> searchByHashtag(@RequestParam String tag) {
        return searchService.searchByHashtag(tag);
    }
}