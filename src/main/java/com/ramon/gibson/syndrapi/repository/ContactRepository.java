package com.ramon.gibson.syndrapi.repository;


import com.ramon.gibson.syndrapi.model.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {
    List<Contact> findByUsername(String userId);

    Optional<Contact> findByEmailAndUsername(String email, String username);
}