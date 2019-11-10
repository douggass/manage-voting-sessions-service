package com.service.managevotingsessionsservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.service.managevotingsessionsservice.document.SessionDocument;

@Repository
public interface SessionRepository extends ReactiveMongoRepository<SessionDocument, String> {

}
