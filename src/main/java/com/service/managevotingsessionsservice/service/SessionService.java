package com.service.managevotingsessionsservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.service.managevotingsessionsservice.document.SessionDocument;
import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.exception.ApiDataBaseException;
import com.service.managevotingsessionsservice.repository.SessionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;

	public Mono<SessionInformationDto> createSession(SessionCreateDto sessionCreate) {
		return sessionRepository
				.save(SessionDocument.builder().minutesLong(sessionCreate.getAmountMinutes()).UUID(UUID.randomUUID())
						.subject(sessionCreate.getSubjectDescription()).build())
				.onErrorMap(ex -> new ApiDataBaseException(ex.getMessage()))
				.map(sessionDocument -> SessionInformationDto.builder().id(sessionDocument.getUUID()).build());
	}

	public Flux<SessionDocument> getSessions() {
		// sessionRepository.deleteAll().subscribe();
		return sessionRepository.findAll();
	}

	public ResponseEntity<Void> startSession(SessionInformationDto sessionInformation) {
		sessionRepository.findByUUID(sessionInformation.getId())
				.map(sessionDocument -> sessionDocument.toBuilder().start(Instant.now())
						.end(Instant.now().plus(sessionDocument.getMinutesLong(), ChronoUnit.MINUTES)).build())
				.flatMap(sessionRepository::save).onErrorMap(ex -> new ApiDataBaseException(ex.getMessage()))
				.subscribe();
		return ResponseEntity.ok().build();
	}

}
