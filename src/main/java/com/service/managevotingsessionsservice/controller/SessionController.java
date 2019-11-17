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

import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.dto.VoteDto;
import com.service.managevotingsessionsservice.messaging.Producer;
import com.service.managevotingsessionsservice.messaging.SampleMessage;
import com.service.managevotingsessionsservice.messaging.SessionMessageDto;
import com.service.managevotingsessionsservice.service.SessionService;
import com.service.managevotingsessionsservice.type.DecisionType;

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

	@Autowired
	private Producer producer;

	// TODO: delete
	@GetMapping("/v1/associate")
	public Flux<?> getAssociates() {

		producer.send(SampleMessage.builder().id(45).message(SessionMessageDto.builder()
				.uuid(UUID.randomUUID().toString()).decision(DecisionType.NO.getDecisionValue()).build()).build());

		return sessionService.getAssociates();
	}

}
