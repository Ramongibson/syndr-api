package com.ramon.gibson.syndrapi.controller;

import com.ramon.gibson.syndrapi.model.EmailTemplate;
import com.ramon.gibson.syndrapi.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/templates")
@Tag(name = "Email Templates", description = "API for managing email templates")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;


    @PostMapping
    @Operation(summary = "Add a new email template")
    public ResponseEntity<?> addTemplate(@Valid @RequestBody EmailTemplate template) {
        try {
            return ResponseEntity.ok(emailTemplateService.saveTemplate(template));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all email templates")
    public ResponseEntity<List<EmailTemplate>> getAllTemplates() {
        return ResponseEntity.ok(emailTemplateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an email template by ID")
    public ResponseEntity<EmailTemplate> getTemplateById(@PathVariable String id) {
        return ResponseEntity.ok(emailTemplateService.getTemplateById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an email template by ID")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        emailTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}