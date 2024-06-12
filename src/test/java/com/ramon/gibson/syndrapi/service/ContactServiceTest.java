package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.Contact;
import com.ramon.gibson.syndrapi.repository.ContactRepository;
import com.ramon.gibson.syndrapi.util.UserAuthenticationUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    private static MockedStatic<UserAuthenticationUtil> mockedUserAuthenticationUtil;
    @Mock
    private ContactRepository contactRepository;
    @InjectMocks
    private ContactService contactService;

    @BeforeAll
    public static void setUpBeforeClass() {
        mockedUserAuthenticationUtil = mockStatic(UserAuthenticationUtil.class);
    }

    @AfterAll
    public static void tearDownAfterClass() {
        mockedUserAuthenticationUtil.close();
    }

    @BeforeEach
    public void setup() {
        mockedUserAuthenticationUtil.reset();
    }

    @Test
    public void testSaveContact_Success() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenReturn(Contact.builder().id("1").email("test@test.com").username("testuser").build());

        Contact contact = Contact.builder().email("test@test.com").build();
        Contact savedContact = contactService.saveContact(contact);

        assertNotNull(savedContact);
        assertEquals("test@test.com", savedContact.getEmail());
        assertEquals("testuser", savedContact.getUsername());
    }

    @Test
    public void testSaveContact_ContactAlreadyExists() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.of(Contact.builder().email("test@test.com").username("testuser").build()));

        Contact contact = Contact.builder().email("test@test.com").build();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> contactService.saveContact(contact));

        assertEquals("Contact with this email already exists", exception.getMessage());
    }

    @Test
    public void testGetAllContacts() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findByUsername(anyString())).thenReturn(List.of(Contact.builder().email("test@test.com").username("testuser").build()));

        List<Contact> contacts = contactService.getAllContacts();

        assertNotNull(contacts);
        assertEquals(1, contacts.size());
        assertEquals("test@test.com", contacts.get(0).getEmail());
    }

    @Test
    public void testGetContactById_Success() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(Contact.builder().id("1").email("test@test.com").username("testuser").build()));

        Contact contact = contactService.getContactById("1");

        assertNotNull(contact);
        assertEquals("test@test.com", contact.getEmail());
    }

    @Test
    public void testGetContactById_NotFound() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.empty());

        Contact contact = contactService.getContactById("1");

        assertNull(contact);
    }

    @Test
    public void testUpdateContact_Success() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(Contact.builder().id("1").email("test@test.com").username("testuser").build()));
        when(contactRepository.save(any(Contact.class))).thenReturn(Contact.builder().id("1").email("new@test.com").username("testuser").build());

        Contact updatedContact = Contact.builder().email("new@test.com").build();
        Contact savedContact = contactService.updateContact("1", updatedContact);

        assertNotNull(savedContact);
        assertEquals("new@test.com", savedContact.getEmail());
    }

    @Test
    public void testUpdateContact_ContactNotFound() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.empty());

        Contact updatedContact = Contact.builder().email("new@test.com").build();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> contactService.updateContact("1", updatedContact));

        assertEquals("Contact not found", exception.getMessage());
    }

    @Test
    public void testUpdateContact_EmailAlreadyExists() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(Contact.builder().id("1").email("test@test.com").username("testuser").build()));
        when(contactRepository.findByEmailAndUsername(anyString(), anyString())).thenReturn(Optional.of(Contact.builder().email("new@test.com").username("testuser").build()));

        Contact updatedContact = Contact.builder().email("new@test.com").build();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> contactService.updateContact("1", updatedContact));

        assertEquals("Contact with this email already exists", exception.getMessage());
    }

    @Test
    public void testDeleteContact_Success() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.of(Contact.builder().id("1").email("test@test.com").username("testuser").build()));

        contactService.deleteContact("1");

        verify(contactRepository, times(1)).deleteById("1");
    }

    @Test
    public void testDeleteContact_ContactNotFound() {
        when(UserAuthenticationUtil.getAuthenticatedUsername()).thenReturn("testuser");
        when(contactRepository.findById(anyString())).thenReturn(Optional.empty());

        contactService.deleteContact("1");

        verify(contactRepository, never()).deleteById(anyString());
    }
}