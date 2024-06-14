package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.EmailTemplate;
import com.ramon.gibson.syndrapi.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailTemplateServiceTest {

    @Mock
    private EmailTemplateRepository emailTemplateRepository;

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    private EmailTemplate emailTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        emailTemplate = EmailTemplate.builder()
                .id("template1")
                .name("Template 1")
                .username("testUser")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("testUser");
    }

    @Test
    void testSaveTemplate_Success() {
        when(emailTemplateRepository.findByNameAndUsername("Template 1", "testUser")).thenReturn(Optional.empty());
        when(emailTemplateRepository.save(any(EmailTemplate.class))).thenReturn(emailTemplate);

        EmailTemplate result = emailTemplateService.saveTemplate(emailTemplate);

        assertNotNull(result);
        assertEquals("template1", result.getId());
        verify(emailTemplateRepository, times(1)).save(any(EmailTemplate.class));
    }

    @Test
    void testSaveTemplate_AlreadyExists() {
        when(emailTemplateRepository.findByNameAndUsername("Template 1", "testUser")).thenReturn(Optional.of(emailTemplate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            emailTemplateService.saveTemplate(emailTemplate);
        });

        assertEquals("An email template with this name already exists.", exception.getMessage());
    }

    @Test
    void testGetAllTemplates_Success() {
        when(emailTemplateRepository.findByUsername("testUser")).thenReturn(List.of(emailTemplate));

        List<EmailTemplate> result = emailTemplateService.getAllTemplates();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetTemplateById_Success() {
        when(emailTemplateRepository.findById("template1")).thenReturn(Optional.of(emailTemplate));

        EmailTemplate result = emailTemplateService.getTemplateById("template1");

        assertNotNull(result);
        assertEquals("template1", result.getId());
    }

    @Test
    void testGetTemplateById_NotFound() {
        when(emailTemplateRepository.findById("template1")).thenReturn(Optional.empty());

        EmailTemplate result = emailTemplateService.getTemplateById("template1");

        assertNull(result);
    }

    @Test
    void testDeleteTemplate_Success() {
        when(emailTemplateRepository.findById("template1")).thenReturn(Optional.of(emailTemplate));

        emailTemplateService.deleteTemplate("template1");

        verify(emailTemplateRepository, times(1)).deleteById("template1");
    }

    @Test
    void testDeleteTemplate_NotFound() {
        when(emailTemplateRepository.findById("template1")).thenReturn(Optional.empty());

        emailTemplateService.deleteTemplate("template1");

        verify(emailTemplateRepository, never()).deleteById("template1");
    }
}