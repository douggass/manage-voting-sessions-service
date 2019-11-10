package com.service.managevotingsessionsservice.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
				.save(SessionDocument.builder().minutesLong(sessionCreate.getAmountMinutes()).uuid(UUID.randomUUID())
						.subject(sessionCreate.getSubjectDescription()).build())
				.onErrorMap(ex -> new ApiDataBaseException(ex.getMessage()))
				.map(sessionDocument -> SessionInformationDto.builder().id(sessionDocument.getUuid()).build());
	}

	public Flux<SessionDocument> getSessions() {
		return sessionRepository.findAll();
	}

}
