package com.ramon.gibson.syndrapi.repository;

import com.ramon.gibson.syndrapi.model.ContactGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactGroupRepository extends MongoRepository<ContactGroup, String> {
    List<ContactGroup> findByUsername(String username);
    Optional<ContactGroup> findByNameAndUsername(String name, String username
    );
}
