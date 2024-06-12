package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.dto.UserUpdateDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.repository.UserRepository;
import com.ramon.gibson.syndrapi.util.EncryptionUtil;
import com.ramon.gibson.syndrapi.util.UserAuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${encryption_key}")
    private String encryptionKey;

    public User saveUser(UserRegistrationDTO userRegistrationDTO, boolean isAdmin) {
        log.debug("Attempting to save user with username: {}", userRegistrationDTO.getUsername());

        // Check if the user already exists
        if (findByUsername(userRegistrationDTO.getUsername()) != null) {
            log.warn("User with username {} already exists", userRegistrationDTO.getUsername());
            throw new IllegalArgumentException("User with this username already exists.");
        }

        // Check if the user already exists by email
        if (findByEmail(userRegistrationDTO.getEmail()) != null) {
            log.warn("User with email {} already exists", userRegistrationDTO.getEmail());
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String encryptedPassword = EncryptionUtil.encrypt(userRegistrationDTO.getEmailPassword(), encryptionKey);
        User user = User.builder()
                .username(userRegistrationDTO.getUsername())
                .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                .email(userRegistrationDTO.getEmail())
                .emailPassword(encryptedPassword)
                .roles(isAdmin ? Collections.singletonList("ROLE_ADMIN") : Collections.singletonList("ROLE_USER"))
                .build();
        User savedUser = userRepository.save(user);
        log.info("User saved successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    public User saveGmailUser(User user) {
        log.debug("Attempting to save Gmail user with email: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        log.info("Gmail user saved successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(UserUpdateDTO updatedUser) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to update user: {}", username);
        User user = findByUsername(username);
        if (user == null) {
            log.warn("User not found for username: {}", username);
            throw new IllegalArgumentException("User not found.");
        }

        if (StringUtils.isNotBlank(updatedUser.getUsername()) && !StringUtils.equals(user.getUsername(), updatedUser.getUsername())) {
            // Check if the updated username already exists
            if (findByUsername(updatedUser.getUsername()) != null) {
                log.warn("User with username {} already exists", updatedUser.getUsername());
                throw new IllegalArgumentException("User with this username already exists.");
            }
            user.setUsername(updatedUser.getUsername());
        }

        if (StringUtils.isNotBlank(updatedUser.getEmail()) && !StringUtils.equals(user.getEmail(), updatedUser.getEmail())) {
            // Check if the user already exists by email
            if (findByEmail(updatedUser.getEmail()) != null) {
                log.warn("User with email {} already exists", updatedUser.getEmail());
                throw new IllegalArgumentException("User with this email already exists.");
            }
            user.setEmail(updatedUser.getEmail());
        }

        if (StringUtils.isNotBlank(updatedUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (StringUtils.isNotBlank(updatedUser.getEmailPassword())) {
            String encryptedPassword = EncryptionUtil.encrypt(updatedUser.getEmailPassword(), encryptionKey);
            log.debug("Updating email password for user: {}", username);
            user.setEmailPassword(encryptedPassword);
        }

        User updated = userRepository.save(user);
        log.info("User updated successfully with id: {}", updated.getId());
        return updated;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}