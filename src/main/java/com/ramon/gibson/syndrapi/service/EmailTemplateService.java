package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.EmailTemplate;
import com.ramon.gibson.syndrapi.repository.EmailTemplateRepository;
import com.ramon.gibson.syndrapi.util.UserAuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplate saveTemplate(EmailTemplate template) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        template.setUsername(username);

        log.debug("Attempting to save email template with name: {} for user: {}", template.getName(), username);

        // Check if a template with the same name exists for the user
        if (emailTemplateRepository.findByNameAndUsername(template.getName(), username).isPresent()) {
            log.warn("An email template with name: {} already exists for user: {}", template.getName(), username);
            throw new IllegalArgumentException("An email template with this name already exists.");
        }

        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        log.info("Email template saved successfully with id: {}", savedTemplate.getId());
        return savedTemplate;
    }

    public List<EmailTemplate> getAllTemplates() {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching all email templates for user: {}", username);
        return emailTemplateRepository.findByUsername(username);
    }

    public EmailTemplate getTemplateById(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching email template with id: {} for user: {}", id, username);
        EmailTemplate template = emailTemplateRepository.findById(id).orElse(null);
        if (template != null && StringUtils.equals(username, template.getUsername())) {
            return template;
        } else {
            log.warn("Email template with id: {} not found or does not belong to user: {}", id, username);
            return null;
        }
    }

    public void deleteTemplate(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to delete email template with id: {} for user: {}", id, username);
        EmailTemplate template = emailTemplateRepository.findById(id).orElse(null);
        if (template != null && StringUtils.equals(username, template.getUsername())) {
            emailTemplateRepository.deleteById(id);
            log.info("Email template with id: {} deleted successfully", id);
        } else {
            log.warn("Email template with id: {} not found or does not belong to user: {}", id, username);
        }
    }
}