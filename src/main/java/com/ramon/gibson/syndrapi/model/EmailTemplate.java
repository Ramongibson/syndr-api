package com.ramon.gibson.syndrapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "email_templates")
@Data
@Builder
public class EmailTemplate {
    @Id
    @Schema(hidden = true)
    private String id;

    @Schema(hidden = true)
    private String username;

    @NotBlank(message = "Email template name is required")
    @Size(min = 1, max = 50, message = "Email template name must be between 1 and 25 characters")
    private String name;

    @NotBlank(message = "Email subject is required")
    private String subject;

    @NotBlank(message = "Email body is required")
    private String body;
}