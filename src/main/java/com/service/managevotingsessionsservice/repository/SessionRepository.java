package com.service.managevotingsessionsservice.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.service.managevotingsessionsservice.document.SessionDocument;

import reactor.core.publisher.Mono;

@Repository
public interface SessionRepository extends ReactiveMongoRepository<SessionDocument, String> {

	public Mono<SessionDocument> findByUUID(UUID uuid);
}