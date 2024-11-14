package org.example.hlcoursesappserver.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Implement the logic to load user details by username
        // For example, you can fetch user details from the database
        // and return an instance of a class that implements UserDetails
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}