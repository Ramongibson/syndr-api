package com.ramon.gibson.syndrapi.repository;

import com.ramon.gibson.syndrapi.model.EmailTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends MongoRepository<EmailTemplate, String> {
    List<EmailTemplate> findByUsername(String username);
    Optional<EmailTemplate> findByNameAndUsername(String name, String username);
}
