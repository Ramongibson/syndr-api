package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User saveUser(UserRegistrationDTO userRegistrationDTO, boolean isAdmin) {
        // Check if the user already exists
        if (userRepository.findByUsername(userRegistrationDTO.getUsername()) != null) {
            throw new IllegalArgumentException("User with this username already exists.");
        }

        // Check if the user already exists by email
        if (userRepository.findByEmail(userRegistrationDTO.getEmail()) != null) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setEmail(userRegistrationDTO.getEmail());
        user.setEmailPassword(passwordEncoder.encode(userRegistrationDTO.getEmailPassword()));
        user.setRoles(isAdmin ? Collections.singletonList("ROLE_ADMIN") : Collections.singletonList("ROLE_USER"));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
