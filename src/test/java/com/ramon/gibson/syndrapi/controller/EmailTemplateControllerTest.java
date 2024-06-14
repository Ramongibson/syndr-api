package com.ramon.gibson.syndrapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramon.gibson.syndrapi.model.EmailTemplate;
import com.ramon.gibson.syndrapi.service.EmailTemplateService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmailTemplateControllerTest {

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private EmailTemplateController emailTemplateController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailTemplateController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddTemplate_Success() throws Exception {
        EmailTemplate emailTemplate = EmailTemplate.builder()
                .name("Template 1")
                .subject("Test Subject")
                .body("This is a test body.")
                .build();

        when(emailTemplateService.saveTemplate(any(EmailTemplate.class))).thenReturn(emailTemplate);

        mockMvc.perform(post("/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailTemplate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Template 1"))
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.body").value("This is a test body."));

        verify(emailTemplateService, times(1)).saveTemplate(any(EmailTemplate.class));
    }

    @Test
    void testAddTemplate_AlreadyExists() throws Exception {
        EmailTemplate emailTemplate = EmailTemplate.builder()
                .name("Template 1")
                .subject("Test Subject")
                .body("This is a test body.")
                .build();

        when(emailTemplateService.saveTemplate(any(EmailTemplate.class))).thenThrow(new IllegalArgumentException("An email template with this name already exists."));

        mockMvc.perform(post("/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailTemplate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("An email template with this name already exists."));

        verify(emailTemplateService, times(1)).saveTemplate(any(EmailTemplate.class));
    }

    @Test
    void testGetAllTemplates_Success() throws Exception {
        EmailTemplate emailTemplate = EmailTemplate.builder()
                .name("Template 1")
                .subject("Test Subject")
                .body("This is a test body.")
                .build();

        when(emailTemplateService.getAllTemplates()).thenReturn(Collections.singletonList(emailTemplate));

        mockMvc.perform(get("/templates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Template 1"))
                .andExpect(jsonPath("$[0].subject").value("Test Subject"))
                .andExpect(jsonPath("$[0].body").value("This is a test body."));

        verify(emailTemplateService, times(1)).getAllTemplates();
    }

    @Test
    void testGetTemplateById_Success() throws Exception {
        EmailTemplate emailTemplate = EmailTemplate.builder()
                .id("template1")
                .name("Template 1")
                .subject("Test Subject")
                .body("This is a test body.")
                .build();

        when(emailTemplateService.getTemplateById("template1")).thenReturn(emailTemplate);

        mockMvc.perform(get("/templates/template1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Template 1"))
                .andExpect(jsonPath("$.subject").value("Test Subject"))
                .andExpect(jsonPath("$.body").value("This is a test body."));

        verify(emailTemplateService, times(1)).getTemplateById("template1");
    }

    @Test
    void testDeleteTemplate_Success() throws Exception {
        doNothing().when(emailTemplateService).deleteTemplate("template1");

        mockMvc.perform(delete("/templates/template1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(emailTemplateService, times(1)).deleteTemplate("template1");
    }
}