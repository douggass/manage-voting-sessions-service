package com.service.managevotingsessionsservice.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.service.managevotingsessionsservice.document.AssociateDocument;
import com.service.managevotingsessionsservice.document.SessionDocument;
import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.dto.VoteDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionService {

	public Mono<SessionInformationDto> createSession(final SessionCreateDto sessionCreate);

	public Mono<ResponseEntity<Void>> startSession(final SessionInformationDto sessionInformation);

	public Mono<ResponseEntity<Void>> vote(final VoteDto vote, final UUID sessionUUID);

	public Flux<AssociateDocument> getAllAssociates();

	public Flux<SessionDocument> getAllSessions();

}
