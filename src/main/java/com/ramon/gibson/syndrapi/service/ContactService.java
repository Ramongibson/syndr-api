package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.Contact;
import com.ramon.gibson.syndrapi.repository.ContactRepository;
import com.ramon.gibson.syndrapi.util.UserAuthenticationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact saveContact(Contact contact) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        contact.setUsername(username);
        log.debug("Attempting to save contact with email: {} for user: {}", contact.getEmail(), username);
        Optional<Contact> existingContact = contactRepository.findByEmailAndUsername(contact.getEmail(), username);
        if (existingContact.isPresent()) {
            log.error("Contact with email: {} already exists for user: {}", contact.getEmail(), username);
            throw new IllegalArgumentException("Contact with this email already exists");
        }
        Contact savedContact = contactRepository.save(contact);
        return savedContact;
    }

    public List<Contact> getAllContacts() {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching all contacts for user: {}", username);
        return contactRepository.findByUsername(username);
    }

    public Contact getContactById(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching contact with id: {} for user: {}", id, username);
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null && StringUtils.equals(username, contact.getUsername())) {
            return contact;
        } else {
            log.warn("Contact with id: {} not found or does not belong to user: {}", id, username);
            return null;
        }
    }

    public Contact updateContact(String id, Contact updatedContact) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to update contact with id: {} for user: {}", id, username);
        Contact contact = getContactById(id);
        if (contact == null) {
            log.error("Contact with id: {} not found for user: {}", id, username);
            throw new IllegalArgumentException("Contact not found");
        }
        if (!StringUtils.equals(contact.getEmail(), updatedContact.getEmail())) {
            Optional<Contact> existingContact = contactRepository.findByEmailAndUsername(updatedContact.getEmail(), username);
            if (existingContact.isPresent()) {
                log.error("Contact with email: {} already exists for user: {}", updatedContact.getEmail(), username);
                throw new IllegalArgumentException("Contact with this email already exists");
            }
        }
        updatedContact.setId(id);
        updatedContact.setUsername(username);
        Contact savedContact = contactRepository.save(updatedContact);
        log.info("Contact updated successfully with id: {}", savedContact.getId());
        return savedContact;
    }

    public void deleteContact(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to delete contact with id: {} for user: {}", id, username);
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null && StringUtils.equals(username, contact.getUsername())) {
            contactRepository.deleteById(id);
            log.info("Contact with id: {} deleted successfully", id);
        } else {
            log.warn("Contact with id: {} not found or does not belong to user: {}", id, username);
        }
    }
}