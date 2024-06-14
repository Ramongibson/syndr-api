package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.ContactGroup;
import com.ramon.gibson.syndrapi.repository.ContactGroupRepository;
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
public class ContactGroupServiceTest {

    @Mock
    private ContactGroupRepository contactGroupRepository;

    @InjectMocks
    private ContactGroupService contactGroupService;

    private ContactGroup contactGroup;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        contactGroup = ContactGroup.builder()
                .id("group1")
                .name("Group 1")
                .username("testUser")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("testUser");
    }

    @Test
    void testSaveGroup_Success() {
        when(contactGroupRepository.findByNameAndUsername("Group 1", "testUser")).thenReturn(Optional.empty());
        when(contactGroupRepository.save(any(ContactGroup.class))).thenReturn(contactGroup);

        ContactGroup result = contactGroupService.saveGroup(contactGroup);

        assertNotNull(result);
        assertEquals("group_Group 1_testUser", result.getId());
        verify(contactGroupRepository, times(1)).save(any(ContactGroup.class));
    }

    @Test
    void testSaveGroup_AlreadyExists() {
        when(contactGroupRepository.findByNameAndUsername("Group 1", "testUser")).thenReturn(Optional.of(contactGroup));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contactGroupService.saveGroup(contactGroup);
        });

        assertEquals("A contact group with this name already exists.", exception.getMessage());
    }

    @Test
    void testGetAllGroups_Success() {
        when(contactGroupRepository.findByUsername("testUser")).thenReturn(List.of(contactGroup));

        List<ContactGroup> result = contactGroupService.getAllGroups();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetGroupById_Success() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.of(contactGroup));

        ContactGroup result = contactGroupService.getGroupById("group1");

        assertNotNull(result);
        assertEquals("group1", result.getId());
    }

    @Test
    void testGetGroupById_NotFound() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.empty());

        ContactGroup result = contactGroupService.getGroupById("group1");

        assertNull(result);
    }

    @Test
    void testUpdateGroup_Success() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.of(contactGroup));
        when(contactGroupRepository.save(any(ContactGroup.class))).thenReturn(contactGroup);

        contactGroup.setName("New Group");
        ContactGroup result = contactGroupService.updateGroup("group1", contactGroup);

        assertNotNull(result);
        assertEquals("group1", result.getId());
        verify(contactGroupRepository, times(1)).save(any(ContactGroup.class));
    }

    @Test
    void testUpdateGroup_GroupNotFound() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contactGroupService.updateGroup("group1", contactGroup);
        });

        assertEquals("Group not found", exception.getMessage());
    }

    @Test
    void testUpdateGroup_NameAlreadyExists() {
        ContactGroup existingGroup = ContactGroup.builder()
                .id("group2")
                .name("New Group")
                .username("testUser")
                .build();

        when(contactGroupRepository.findById("group1")).thenReturn(Optional.of(contactGroup));
        when(contactGroupRepository.findByNameAndUsername("New Group", "testUser")).thenReturn(Optional.ofNullable(contactGroup));

        //contactGroup.setName("New Group");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contactGroupService.updateGroup("group1", existingGroup);
        });

        assertEquals("A contact group with this name already exists.", exception.getMessage());
    }

    @Test
    void testDeleteGroup_Success() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.of(contactGroup));

        contactGroupService.deleteGroup("group1");

        verify(contactGroupRepository, times(1)).deleteById("group1");
    }

    @Test
    void testDeleteGroup_NotFound() {
        when(contactGroupRepository.findById("group1")).thenReturn(Optional.empty());

        contactGroupService.deleteGroup("group1");

        verify(contactGroupRepository, never()).deleteById("group1");
    }
}