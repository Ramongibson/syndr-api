package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.dto.UserUpdateDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final String encryptionKey = "TestEncryptionKey";
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock(lenient = true)
    private SecurityContext securityContext;
    @Mock(lenient = true)
    private Authentication authentication;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userService, "encryptionKey", encryptionKey);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("testuser");
    }

    @Test
    public void testSaveUser_Success() {
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = User.builder().id("1").build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.saveUser(dto, false);

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveUser_UsernameExists() {
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(User.builder().build());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(dto, false);
        });

        assertEquals("User with this username already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testSaveUser_EmailExists() {
        UserRegistrationDTO dto = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(User.builder().build());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(dto, false);
        });

        assertEquals("User with this email already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_Success() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .password("newpassword")
                .emailPassword("newemailPassword")
                .build();

        User user = User.builder()
                .username("testuser")
                .email("test@test.com")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.findByUsername("newusername")).thenReturn(null);
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User updatedUser = User.builder().id("1").build();
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(dto);

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UsernameExists() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .build();

        User existingUser = User.builder().username("newusername").build();

        when(userRepository.findByUsername("testuser")).thenReturn(User.builder().username("testuser").build());
        when(userRepository.findByUsername("newusername")).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(dto);
        });

        assertEquals("User with this username already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_EmailExists() {
        UserUpdateDTO dto = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .build();

        User existingUser = User.builder().email("newemail@test.com").build();

        when(userRepository.findByUsername("testuser")).thenReturn(User.builder().username("testuser").build());
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(dto);
        });

        assertEquals("User with this email already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testFindByUsername() {
        User user = User.builder().username("testuser").build();

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void testFindByEmail() {
        User user = User.builder().email("test@test.com").build();

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        User result = userService.findByEmail("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}