package com.ramon.gibson.syndrapi.controller;

import com.ramon.gibson.syndrapi.model.ContactGroup;
import com.ramon.gibson.syndrapi.service.ContactGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
@Tag(name = "Contact Groups", description = "API for managing contact groups")
public class ContactGroupController {

    private final ContactGroupService contactGroupService;

    @PostMapping
    @Operation(summary = "Add a new contact group")
    public ResponseEntity<?> addGroup(@Valid @RequestBody ContactGroup group) {
        try {
            return ResponseEntity.ok(contactGroupService.saveGroup(group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all contact groups")
    public ResponseEntity<List<ContactGroup>> getAllGroups() {
        return ResponseEntity.ok(contactGroupService.getAllGroups());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contact group by ID")
    public ResponseEntity<ContactGroup> getGroupById(@PathVariable String id) {
        ContactGroup group = contactGroupService.getGroupById(id);
        return (group != null) ? ResponseEntity.ok(group) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a contact group by ID")
    public ResponseEntity<?> updateGroup(@PathVariable String id, @Valid @RequestBody ContactGroup group) {
        try {
            return ResponseEntity.ok(contactGroupService.updateGroup(id, group));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact group by ID")
    public ResponseEntity<Void> deleteGroup(@PathVariable String id) {
        contactGroupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
