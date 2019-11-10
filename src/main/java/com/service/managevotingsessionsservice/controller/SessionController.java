package com.service.managevotingsessionsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.service.managevotingsessionsservice.document.SessionDocument;
import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.service.SessionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SessionController {

	@Autowired
	private SessionService sessionService;

	@PostMapping("/v1/session")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Mono<SessionInformationDto> createSession(@RequestBody SessionCreateDto sessionCreate) {
		return sessionService.createSession(sessionCreate);
	}

	@PutMapping("/v1/start-session")
	public ResponseEntity<Void> startSession(@RequestBody SessionInformationDto sessionInformation) {
		return sessionService.startSession(sessionInformation);
	}

	@GetMapping("/v1/session")
	public Flux<SessionDocument> getSessions() {
		return sessionService.getSessions();
	}

}
