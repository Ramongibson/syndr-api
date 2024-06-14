package com.ramon.gibson.syndrapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramon.gibson.syndrapi.model.ContactGroup;
import com.ramon.gibson.syndrapi.service.ContactGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ContactGroupControllerTest {

    @Mock
    private ContactGroupService contactGroupService;

    @InjectMocks
    private ContactGroupController contactGroupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactGroupController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddGroup_Success() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.saveGroup(any(ContactGroup.class))).thenReturn(contactGroup);

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Group 1"))
                .andExpect(jsonPath("$.contactIds").isArray())
                .andExpect(jsonPath("$.contactIds[0]").value("contact1"))
                .andExpect(jsonPath("$.contactIds[1]").value("contact2"));

        verify(contactGroupService, times(1)).saveGroup(any(ContactGroup.class));
    }

    @Test
    void testAddGroup_AlreadyExists() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.saveGroup(any(ContactGroup.class))).thenThrow(new IllegalArgumentException("A contact group with this name already exists."));

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactGroup)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A contact group with this name already exists."));

        verify(contactGroupService, times(1)).saveGroup(any(ContactGroup.class));
    }

    @Test
    void testGetAllGroups_Success() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.getAllGroups()).thenReturn(Collections.singletonList(contactGroup));

        mockMvc.perform(get("/groups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Group 1"))
                .andExpect(jsonPath("$[0].contactIds").isArray())
                .andExpect(jsonPath("$[0].contactIds[0]").value("contact1"))
                .andExpect(jsonPath("$[0].contactIds[1]").value("contact2"));

        verify(contactGroupService, times(1)).getAllGroups();
    }

    @Test
    void testGetGroupById_Success() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .id("group1")
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.getGroupById("group1")).thenReturn(contactGroup);

        mockMvc.perform(get("/groups/group1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Group 1"))
                .andExpect(jsonPath("$.contactIds").isArray())
                .andExpect(jsonPath("$.contactIds[0]").value("contact1"))
                .andExpect(jsonPath("$.contactIds[1]").value("contact2"));

        verify(contactGroupService, times(1)).getGroupById("group1");
    }

    @Test
    void testGetGroupById_NotFound() throws Exception {
        when(contactGroupService.getGroupById("group1")).thenReturn(null);

        mockMvc.perform(get("/groups/group1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(contactGroupService, times(1)).getGroupById("group1");
    }

    @Test
    void testUpdateGroup_Success() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .id("group1")
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.updateGroup(anyString(), any(ContactGroup.class))).thenReturn(contactGroup);

        mockMvc.perform(put("/groups/group1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Group 1"))
                .andExpect(jsonPath("$.contactIds").isArray())
                .andExpect(jsonPath("$.contactIds[0]").value("contact1"))
                .andExpect(jsonPath("$.contactIds[1]").value("contact2"));

        verify(contactGroupService, times(1)).updateGroup(anyString(), any(ContactGroup.class));
    }

    @Test
    void testUpdateGroup_NotFound() throws Exception {
        ContactGroup contactGroup = ContactGroup.builder()
                .id("group1")
                .name("Group 1")
                .contactIds(Arrays.asList("contact1", "contact2"))
                .build();

        when(contactGroupService.updateGroup(anyString(), any(ContactGroup.class))).thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(put("/groups/group1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contactGroup)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Group not found"));

        verify(contactGroupService, times(1)).updateGroup(anyString(), any(ContactGroup.class));
    }

    @Test
    void testDeleteGroup_Success() throws Exception {
        doNothing().when(contactGroupService).deleteGroup("group1");

        mockMvc.perform(delete("/groups/group1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(contactGroupService, times(1)).deleteGroup("group1");
    }
}