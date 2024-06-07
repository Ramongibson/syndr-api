package com.ramon.gibson.syndrapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contacts")
@Data
public class Contact {
    @Id
    @Schema(hidden = true)
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
}


