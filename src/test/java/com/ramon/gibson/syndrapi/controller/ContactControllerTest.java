package com.ramon.gibson.syndrapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramon.gibson.syndrapi.model.Contact;
import com.ramon.gibson.syndrapi.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ContactControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ContactService contactService;
    @InjectMocks
    private ContactController contactController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
    }

    @Test
    void testAddContact_Success() throws Exception {
        Contact contact = Contact.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com")
                .phoneNumber("1234567890")
                .build();
        when(contactService.saveContact(any(Contact.class))).thenReturn(contact);

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void testAddContact_ContactExists() throws Exception {
        Contact contact = Contact.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com")
                .phoneNumber("1234567890")
                .build();
        when(contactService.saveContact(any(Contact.class))).thenThrow(new IllegalArgumentException("Contact with this email already exists"));

        mockMvc.perform(post("/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Contact with this email already exists"));
    }

    @Test
    void testGetAllContacts() throws Exception {
        Contact contact = Contact.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com")
                .phoneNumber("1234567890")
                .build();
        when(contactService.getAllContacts()).thenReturn(Collections.singletonList(contact));

        mockMvc.perform(get("/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void testGetContactById_Success() throws Exception {
        Contact contact = Contact.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@test.com")
                .phoneNumber("1234567890")
                .build();
        when(contactService.getContactById(anyString())).thenReturn(contact);

        mockMvc.perform(get("/contacts/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void testUpdateContact_Success() throws Exception {
        Contact contact = Contact.builder()
                .firstName("John")
                .lastName("Doe")
                .email("updated@test.com")
                .phoneNumber("1234567890")
                .build();
        when(contactService.updateContact(anyString(), any(Contact.class))).thenReturn(contact);

        mockMvc.perform(put("/contacts/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void testDeleteContact_Success() throws Exception {
        doNothing().when(contactService).deleteContact(anyString());

        mockMvc.perform(delete("/contacts/{id}", "1"))
                .andExpect(status().isNoContent());
    }
}