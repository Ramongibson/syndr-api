package com.ramon.gibson.syndrapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdate {
    @Size(max = 15, message = "Username must be 15 characters or less")
    private String username;

    private String password;

    @Email(message = "Email should be valid")
    private String email;

    private String emailPassword;
}