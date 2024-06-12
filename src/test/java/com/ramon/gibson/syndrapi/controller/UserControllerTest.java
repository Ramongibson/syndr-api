package com.ramon.gibson.syndrapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.dto.UserUpdateDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .verifyPassword("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        User user = User.builder().id("1").username("testuser").email("test@test.com").build();

        when(userService.saveUser(any(UserRegistrationDTO.class), any(Boolean.class))).thenReturn(user);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegistrationDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@test.com")));
    }

    @Test
    public void testCreateUser_UsernameExists() throws Exception {
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        when(userService.saveUser(any(UserRegistrationDTO.class), any(Boolean.class)))
                .thenThrow(new IllegalArgumentException("User with this username already exists."));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with this username already exists."));
    }

    @Test
    public void testCreateUser_EmailExists() throws Exception {
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                .username("testuser")
                .password("password")
                .email("test@test.com")
                .emailPassword("emailPassword")
                .build();

        when(userService.saveUser(any(UserRegistrationDTO.class), any(Boolean.class)))
                .thenThrow(new IllegalArgumentException("User with this email already exists."));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRegistrationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with this email already exists."));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .password("newpassword")
                .emailPassword("newemailPassword")
                .build();

        User user = User.builder().id("1").username("newusername").email("newemail@test.com").build();

        when(userService.updateUser(any(UserUpdateDTO.class))).thenReturn(user);

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newusername")))
                .andExpect(jsonPath("$.email", is("newemail@test.com")));
    }

    @Test
    public void testUpdateUser_UsernameExists() throws Exception {
        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .password("newpassword")
                .emailPassword("newemailPassword")
                .build();

        when(userService.updateUser(any(UserUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("User with this username already exists."));

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with this username already exists."));
    }

    @Test
    public void testUpdateUser_EmailExists() throws Exception {
        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .username("newusername")
                .email("newemail@test.com")
                .password("newpassword")
                .emailPassword("newemailPassword")
                .build();

        when(userService.updateUser(any(UserUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("User with this email already exists."));

        mockMvc.perform(put("/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with this email already exists."));
    }
}