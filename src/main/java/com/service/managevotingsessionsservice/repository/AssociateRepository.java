package com.service.managevotingsessionsservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.service.managevotingsessionsservice.document.AssociateDocument;

import reactor.core.publisher.Mono;

public interface AssociateRepository extends ReactiveMongoRepository<AssociateDocument, String> {

	public Mono<AssociateDocument> findByIdentifier(final String identifier);
}
