package com.ramon.gibson.syndrapi.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@SuperBuilder
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String email;
    private String emailPassword;
    private List<String> roles;
}