package com.revconnect.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {
            	String email = jwtUtil.extractEmail(token);

            	User user = userRepository.findByEmail(email).orElse(null);

            	if (user != null) {

            	    UsernamePasswordAuthenticationToken authentication =
            	            new UsernamePasswordAuthenticationToken(
            	                    email,
            	                    null,
            	                    Collections.singletonList(
            	                            new SimpleGrantedAuthority("ROLE_" + user.getRole())
            	                    )
            	            );

            	    SecurityContextHolder.getContext().setAuthentication(authentication);
            	}

            } catch (Exception e) {
                System.out.println("Invalid JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}