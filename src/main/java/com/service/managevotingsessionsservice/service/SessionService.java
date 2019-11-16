package com.service.managevotingsessionsservice.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.service.managevotingsessionsservice.client.UserInfoClient;
import com.service.managevotingsessionsservice.document.AssociateDocument;
import com.service.managevotingsessionsservice.document.SessionDocument;
import com.service.managevotingsessionsservice.document.VoteDocument;
import com.service.managevotingsessionsservice.dto.SessionCreateDto;
import com.service.managevotingsessionsservice.dto.SessionInformationDto;
import com.service.managevotingsessionsservice.dto.VoteDto;
import com.service.managevotingsessionsservice.dto.userinfo.UserStatusDto;
import com.service.managevotingsessionsservice.exception.ApiBusinessException;
import com.service.managevotingsessionsservice.exception.ApiDataBaseException;
import com.service.managevotingsessionsservice.exception.ApiNoDataException;
import com.service.managevotingsessionsservice.exception.ClientException;
import com.service.managevotingsessionsservice.repository.AssociateRepository;
import com.service.managevotingsessionsservice.repository.SessionRepository;
import com.service.managevotingsessionsservice.type.UserVoteType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SessionService {

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private UserInfoClient userInfoClient;

	private static final String NO_SESSION_FOR_THE_UUID = "No session for the UUID";
	private static final String SESSION_HAS_ALREADY_STARTED = "Session has already started";
	private static final String USER_UNABLE_TO_VOTE = "User unable to vote";
	private static final String SESSION_NOT_STARTED = "Session not started";
	private static final String SESSION_IS_CLOSED = "Session is closed";
	private static final String ASSOCIATE_HAS_ALREADY_VOTE = "Associate has already vote";
	private static final String IDENTIFIER_NOT_VALID = "Identifier not valid";
	private static final String CLIENT_API_ERROR = "Client API error";
	private static final String DATABASE_ERROR = "Database error";

	public Mono<SessionInformationDto> createSession(final SessionCreateDto sessionCreate) {
		return sessionRepository.save(SessionDocument.builder().minutesLong(sessionCreate.getAmountMinutes())
				.uuid(UUID.randomUUID()).subject(sessionCreate.getSubjectDescription()).build()).onErrorMap(ex -> {
					log.info("Database error: {}", ex);
					return new ApiDataBaseException(DATABASE_ERROR);
				}).map(sessionDocument -> SessionInformationDto.builder().id(sessionDocument.getUuid()).build());
	}

	public Mono<ResponseEntity<Void>> startSession(final SessionInformationDto sessionInformation) {
		return sessionRepository.findByUuid(sessionInformation.getId())
				.switchIfEmpty(
						Mono.error(() -> new ApiNoDataException(NO_SESSION_FOR_THE_UUID))
				)
				.flatMap(sessionDocument -> {
					if (Objects.nonNull(sessionDocument.getStart())) {
						return Mono.error(new ApiBusinessException(SESSION_HAS_ALREADY_STARTED));
					}
					return Mono.just(sessionDocument);
				})
				.map(sessionDocument -> sessionDocument.toBuilder()
						.start(OffsetDateTime.now(ZoneOffset.UTC).toInstant())
						.end(OffsetDateTime.now(ZoneOffset.UTC).toInstant().plus(sessionDocument.getMinutesLong(),
								ChronoUnit.MINUTES))
						.build())
				.flatMap(sessionRepository::save).map(item -> ResponseEntity.ok().build());
	}

	public Mono<ResponseEntity<Void>> vote(final VoteDto vote, final UUID sessionId) {
		return this.users(vote).flatMap(userStatus -> {
			if (UserVoteType.UNABLE_TO_VOTE.getValue().equals(userStatus.getStatus().getValue())) {
				return Mono.error(new ApiBusinessException(USER_UNABLE_TO_VOTE));
			}
			return validSessionDocument(sessionId);
		}).flatMap(sessionDocument -> this.saveAssociateVote(sessionDocument, vote))
				.map(sessionDocument -> ResponseEntity.status(HttpStatus.CREATED).build());
	}

	private Mono<SessionDocument> validSessionDocument(final UUID sessionId) {
		return sessionRepository.findByUuid(sessionId)
				.switchIfEmpty(Mono.error(() -> new ApiNoDataException(NO_SESSION_FOR_THE_UUID)))
				.flatMap(sessionDocument -> {
					if (Objects.isNull(sessionDocument.getStart())) {
						return Mono.error(new ApiBusinessException(SESSION_NOT_STARTED));
					}
					if (Objects.nonNull(sessionDocument.getEnd())
							&& OffsetDateTime.now(ZoneOffset.UTC).toInstant().isAfter(sessionDocument.getEnd())) {
						return Mono.error(new ApiBusinessException(SESSION_IS_CLOSED));
					}
					return Mono.just(sessionDocument);
				});
	}

	private Mono<AssociateDocument> saveAssociateVote(final SessionDocument sessionDocument, final VoteDto vote) {
		return associateRepository.findByIdentifier(vote.getIdentifier())
				.switchIfEmpty(Mono.defer(() -> associateRepository
						.save(AssociateDocument.builder().identifier(vote.getIdentifier()).build())))
				.flatMap(associateDocument -> {
					List<VoteDocument> associateVotes = Optional.ofNullable(associateDocument.getVotes())
							.orElse(Collections.emptyList()).stream()
							.filter(voteDocument -> sessionDocument.getId().equals(voteDocument.getSession().getId()))
							.collect(Collectors.toList());
					if (!associateVotes.isEmpty()) {
						return Mono.error(new ApiBusinessException(ASSOCIATE_HAS_ALREADY_VOTE));
					}
					VoteDocument voteDocument = VoteDocument.builder().decision(vote.getDecision())
							.session(sessionDocument).build();

					return Mono.just(this.buildAssociateVote(associateDocument, voteDocument));
				}).flatMap(this.associateRepository::save);
	}

	private AssociateDocument buildAssociateVote(final AssociateDocument associateDocument,
			final VoteDocument voteDocument) {
		return associateDocument.toBuilder().votes(Optional.ofNullable(associateDocument.getVotes()).map(listVotes -> {
			listVotes.add(voteDocument);
			return listVotes;
		}).orElse(Collections.singletonList(voteDocument))).build();
	}

	private Mono<UserStatusDto> users(final VoteDto vote) {
		try {
			return userInfoClient.users(vote.getIdentifier())
					.switchIfEmpty(Mono.error(new ApiNoDataException(IDENTIFIER_NOT_VALID)));
		} catch (Exception e) {
			log.info("Error on get users: {}", e);
			throw new ClientException(CLIENT_API_ERROR);
		}
	}
}
