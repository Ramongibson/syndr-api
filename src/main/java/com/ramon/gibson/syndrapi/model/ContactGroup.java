package com.ramon.gibson.syndrapi.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "contact_groups")
@Data
@Builder
public class ContactGroup {
    @Id
    @Schema(hidden = true)
    private String id;

    @Schema(hidden = true)
    private String username;

    @NotBlank(message = "Contact group name is required")
    @Size(min = 1, max = 25, message = "Contact group name must be between 1 and 25 characters")
    private String name;

    @NotEmpty(message = "Contact IDs are required")
    private List<String> contactIds;

}