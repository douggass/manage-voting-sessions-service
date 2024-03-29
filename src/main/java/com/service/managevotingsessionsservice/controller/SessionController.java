package com.service.managevotingsessionsservice.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.service.managevotingsessionsservice.document.AssociateDocument;
import com.service.managevotingsessionsservice.document.SessionDocument;
import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.dto.VoteDto;
import com.service.managevotingsessionsservice.service.SessionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SessionController {

	@Autowired
	private SessionService sessionService;

	@PostMapping("/v1/session")
	@ResponseStatus(value = HttpStatus.CREATED)
	public @ResponseBody Mono<SessionInformationDto> createSession(@RequestBody SessionCreateDto sessionCreate) {
		return sessionService.createSession(sessionCreate);
	}

	@PostMapping("/v1/session/{sessionId}/vote")
	public @ResponseBody Mono<ResponseEntity<Void>> vote(@PathVariable("sessionId") UUID sessionUUID,
			@RequestBody @Valid VoteDto vote) {
		return sessionService.vote(vote, sessionUUID);
	}

	@PutMapping("/v1/start-session")
	public @ResponseBody Mono<ResponseEntity<Void>> startSession(
			@RequestBody SessionInformationDto sessionInformation) {
		return sessionService.startSession(sessionInformation);
	}

	// TODO: refactor to return a dto
	@GetMapping("/v1/session")
	public Flux<SessionDocument> getAllSession() {
		return sessionService.getAllSessions();
	}

	// TODO: refactor to return a dto
	@GetMapping("/v1/associate")
	public Flux<AssociateDocument> getAllAssociates() {
		return sessionService.getAllAssociates();
	}

}
