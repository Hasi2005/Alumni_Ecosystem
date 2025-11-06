package com.example.auth_service.service;

import com.example.auth_service.models.User;
import com.example.auth_service.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service // Marks this class as a service component for dependency injection
public class UserService implements UserDetailsService {

    private final UserRepo userRepo; // Injects the UserRepo for accessing user data
    private final PasswordEncoder passwordEncoder; // Password encoder for hashing passwords

    // Constructor for dependency injection
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieves user details by username from the database
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String create(String username, String password, String authority) {
        String auth = (authority == null || authority.isBlank()) ? "student" : authority.trim().toLowerCase(Locale.ROOT);
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // Encrypts the password
                .authorities(auth) // Assigns default authority
                .build();

        // Saves the new user to the database
        userRepo.save(user);

        return "Create Successfully !"; // Returns a success message
    }
}
