package com.revconnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;

@Service
public class SearchService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Post> searchPosts(String keyword) {
        return postRepository.findByContentContainingIgnoreCase(keyword);
    }

    // Search users by username OR name
    public List<User> searchUsers(String query) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
        // Remove passwords before returning
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    public List<Post> searchByHashtag(String tag) {
        return postRepository.findByHashtag(tag);
    }
}