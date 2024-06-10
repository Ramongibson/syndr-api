package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.Contact;
import com.ramon.gibson.syndrapi.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact saveContact(Contact contact) {
        String userId = getAuthenticatedUserId();
        contact.setUserId(userId);
        Optional<Contact> existingContact = contactRepository.findByEmailAndUserId(contact.getEmail(), userId);
        if (existingContact.isPresent()) {
            throw new IllegalArgumentException("Contact with this email already exists");
        }
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts() {
        System.out.println(getAuthenticatedUserId());
        String userId = getAuthenticatedUserId();
        return contactRepository.findByUserId(userId);
    }

    public Contact getContactById(String id) {
        String userId = getAuthenticatedUserId();
        Contact contact = contactRepository.findById(id).orElse(null);
        return (contact != null && userId.equals(contact.getUserId())) ? contact : null;
    }

    public Contact updateContact(String id, Contact contact) {
        contact.setId(id);
        contact.setUserId(getAuthenticatedUserId());
        return contactRepository.save(contact);
    }

    public void deleteContact(String id) {
        String userId = getAuthenticatedUserId();
        Contact contact = contactRepository.findById(id).orElse(null);
        if (contact != null && userId.equals(contact.getUserId())) {
            contactRepository.deleteById(id);
        }
    }

    private String getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}

