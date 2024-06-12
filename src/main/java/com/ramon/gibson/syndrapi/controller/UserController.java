package com.ramon.gibson.syndrapi.controller;

import com.ramon.gibson.syndrapi.dto.UserRegistrationDTO;
import com.ramon.gibson.syndrapi.dto.UserUpdateDTO;
import com.ramon.gibson.syndrapi.model.User;
import com.ramon.gibson.syndrapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Create a new user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            User newUser = userService.saveUser(userRegistrationDTO, false);
            return ResponseEntity.ok(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing user")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            User updatedUser = userService.updateUser(userUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}