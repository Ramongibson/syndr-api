package com.ramon.gibson.syndrapi.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistration {

    @NotBlank(message = "Username is required")
    @Size(max = 15, message = "Username must be 15 characters or less")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Verify password is required")
    private String verifyPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Email password is required")
    private String emailPassword;

    @NotBlank(message = "Verify email password is required")
    private String verifyEmailPassword;

    @AssertTrue(message = "Password does not match")
    public boolean isPassword() {
        return password.equals(verifyPassword);
    }

    @AssertTrue(message = "Email password does not match")
    public boolean isEmailPassword() {
        return emailPassword.equals(verifyEmailPassword);
    }

}