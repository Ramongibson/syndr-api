package com.ramon.gibson.syndrapi.controller;

import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<User> createAdmin(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        return ResponseEntity.ok(userService.saveUser(userRegistrationDTO, true));
    }
}