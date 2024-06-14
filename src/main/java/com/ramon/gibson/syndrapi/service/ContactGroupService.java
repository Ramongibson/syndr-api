package com.ramon.gibson.syndrapi.service;

import com.ramon.gibson.syndrapi.model.ContactGroup;
import com.ramon.gibson.syndrapi.repository.ContactGroupRepository;
import com.ramon.gibson.syndrapi.util.UserAuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactGroupService {

    private final ContactGroupRepository contactGroupRepository;

    public ContactGroup saveGroup(ContactGroup group) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        group.setUsername(username);
        group.setId("group_" + group.getName() + "_" + username);

        log.debug("Attempting to save group with name: {} for user: {}", group.getName(), username);

        // Check if a group with the same name exists for the user
        if (contactGroupRepository.findByNameAndUsername(group.getName(), username).isPresent()) {
            log.warn("A contact group with name: {} already exists for user: {}", group.getName(), username);
            throw new IllegalArgumentException("A contact group with this name already exists.");
        }

        ContactGroup savedGroup = contactGroupRepository.save(group);
        log.info("Contact group saved successfully with id: {}", savedGroup.getId());
        return savedGroup;
    }

    public List<ContactGroup> getAllGroups() {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching all groups for user: {}", username);
        return contactGroupRepository.findByUsername(username);
    }

    public ContactGroup getGroupById(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Fetching group with id: {} for user: {}", id, username);
        ContactGroup group = contactGroupRepository.findById(id).orElse(null);
        if (group != null && StringUtils.equals(username, group.getUsername())) {
            return group;
        } else {
            log.warn("Group with id: {} not found or does not belong to user: {}", id, username);
            return null;
        }
    }

    public ContactGroup updateGroup(String id, ContactGroup group) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to update group with id: {} for user: {}", id, username);
        ContactGroup existingGroup = getGroupById(id);
        if (existingGroup == null) {
            log.warn("Group with id: {} not found for user: {}", id, username);
            throw new IllegalArgumentException("Group not found");
        }
        if (!StringUtils.equals(existingGroup.getName(), group.getName())) {
            if (contactGroupRepository.findByNameAndUsername(group.getName(), username).isPresent()) {
                log.warn("A contact group with name: {} already exists for user: {}", group.getName(), username);
                throw new IllegalArgumentException("A contact group with this name already exists.");
            }
        }
        group.setId(id);
        group.setUsername(username);
        ContactGroup savedGroup = contactGroupRepository.save(group);
        log.info("Contact group updated successfully");
        return savedGroup;
    }

    public void deleteGroup(String id) {
        String username = UserAuthenticationUtil.getAuthenticatedUsername();
        log.debug("Attempting to delete group with id: {} for user: {}", id, username);
        ContactGroup group = contactGroupRepository.findById(id).orElse(null);
        if (group != null && StringUtils.equals(username, group.getUsername())) {
            contactGroupRepository.deleteById(id);
            log.info("Contact group with id: {} deleted successfully", id);
        } else {
            log.warn("Group with id: {} not found or does not belong to user: {}", id, username);
        }
    }
}