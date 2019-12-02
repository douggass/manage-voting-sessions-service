package com.service.managevotingsessionsservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

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
import com.service.managevotingsessionsservice.messaging.Producer;
import com.service.managevotingsessionsservice.repository.AssociateRepository;
import com.service.managevotingsessionsservice.repository.SessionRepository;
import com.service.managevotingsessionsservice.type.DecisionType;
import com.service.managevotingsessionsservice.type.UserVoteType;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class SessionServiceTest {

	@InjectMocks
	private SessionServiceImp sessionService;

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private AssociateRepository associateRepository;

	@Mock
	private UserInfoClient userInfoClient;

	@Mock
	private Producer producer;

	private static final String SUBJECT_DESCRIPTION = "Assunto da votação";
	private static final String ANY_DOCUMENT_ID = "2";
	private static final String ANY_IDENTIFIER = "20023138068";
	private static final Integer MINUTES_LONG = 25;
	private static final UUID ANY_UUID = UUID.fromString("2eab1bdc-1359-45b2-aaf7-db3c2a909831");

	private static final String NO_SESSION_FOR_THE_UUID = "No session for the UUID";
	private static final String ANY_EXCEPTION_ERROR = "Any exception error";

	private static final String SESSION_HAS_ALREADY_STARTED = "Session has already started";
	private static final String USER_UNABLE_TO_VOTE = "User unable to vote";
	private static final String SESSION_NOT_STARTED = "Session not started";
	private static final String SESSION_IS_CLOSED = "Session is closed";
	private static final String ASSOCIATE_HAS_ALREADY_VOTE = "Associate has already vote";
	private static final String IDENTIFIER_NOT_VALID = "Identifier not valid";
	private static final String CLIENT_API_ERROR = "Client API error";
	private static final String DATABASE_ERROR = "Database error";

	@Test
	public void createSessionTest() {

		SessionDocument sessionDocument = SessionDocument.builder().minutesLong(MINUTES_LONG).uuid(ANY_UUID)
				.subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.save(any())).willReturn(Mono.just(sessionDocument));

		Mono<SessionInformationDto> response = this.sessionService
				.createSession(SessionCreateDto.builder().subjectDescription(SUBJECT_DESCRIPTION).build());

		StepVerifier.create(response).expectSubscription()
				.assertNext(expectSessionDocument -> expectSessionDocument.getId().equals(ANY_UUID)).verifyComplete();
	}

	@Test
	public void createSessionDbErrorTest() {
		given(sessionRepository.save(any())).willThrow(new ApiDataBaseException(DATABASE_ERROR));

		ApiDataBaseException thrown = assertThrows(ApiDataBaseException.class, () -> this.sessionService
				.createSession(SessionCreateDto.builder().subjectDescription(SUBJECT_DESCRIPTION).build()).block());

		assertTrue(thrown.getMessage().contains(DATABASE_ERROR));
	}

	@Test
	public void startSessionTest() {
		SessionDocument sessionDocument = SessionDocument.builder().minutesLong(MINUTES_LONG).uuid(ANY_UUID)
				.subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		given(sessionRepository.save(any())).willReturn(Mono.just(sessionDocument
				.toBuilder().start(OffsetDateTime.now(ZoneOffset.UTC).toInstant()).end(OffsetDateTime
						.now(ZoneOffset.UTC).toInstant().plus(sessionDocument.getMinutesLong(), ChronoUnit.MINUTES))
				.build()));

		Mono<ResponseEntity<Void>> response = this.sessionService
				.startSession(SessionInformationDto.builder().id(ANY_UUID).build());

		StepVerifier.create(response).expectSubscription()
				.assertNext(expectResponseEntity -> expectResponseEntity.getStatusCode().is2xxSuccessful())
				.verifyComplete();
	}

	@Test
	public void startSessionNoSessionForUuidTest() {

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.empty());

		ApiNoDataException thrown = assertThrows(ApiNoDataException.class,
				() -> this.sessionService.startSession(SessionInformationDto.builder().id(ANY_UUID).build()).block());

		assertTrue(thrown.getMessage().contains(NO_SESSION_FOR_THE_UUID));

	}

	@Test
	public void startSessionButItsHasStartedTest() {
		SessionDocument sessionDocument = SessionDocument.builder().minutesLong(MINUTES_LONG).uuid(ANY_UUID)
				.start(Instant.now()).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		ApiBusinessException thrown = assertThrows(ApiBusinessException.class,
				() -> this.sessionService.startSession(SessionInformationDto.builder().id(ANY_UUID).build()).block());

		assertTrue(thrown.getMessage().contains(SESSION_HAS_ALREADY_STARTED));
	}

	@Test
	public void voteWhenAssociateIsRegisteredTest() {

		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		SessionDocument sessionDocument = SessionDocument.builder().id(ANY_DOCUMENT_ID).minutesLong(MINUTES_LONG)
				.uuid(ANY_UUID).start(Instant.now()).start(Instant.now()).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		AssociateDocument associateDocument = AssociateDocument.builder().identifier(ANY_IDENTIFIER).build();

		given(associateRepository.findByIdentifier(ANY_IDENTIFIER)).willReturn(Mono.just(associateDocument));

		VoteDocument voteDocument = VoteDocument.builder().decision(DecisionType.NO).session(sessionDocument).build();

		given(associateRepository.save(any()))
				.willReturn(Mono.just(associateDocument.toBuilder().votes(Arrays.asList(voteDocument)).build()));

		Mono<ResponseEntity<Void>> response = this.sessionService
				.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID);

		StepVerifier.create(response).expectSubscription()
				.assertNext(expectResponseEntity -> expectResponseEntity.getStatusCode().is2xxSuccessful())
				.verifyComplete();

	}

	@Test
	@Disabled("Disabled as it requires two different returns from the associated save method but as the creation date is automatically generated it was not possible to perform the mocks.")
	public void voteWhenAssociateIsNotRegisteredTest() {

		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		SessionDocument sessionDocument = SessionDocument.builder().id(ANY_DOCUMENT_ID).minutesLong(MINUTES_LONG)
				.uuid(ANY_UUID).start(Instant.now()).start(Instant.now()).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		AssociateDocument associateDocument = AssociateDocument.builder().identifier(ANY_IDENTIFIER).build();

		given(associateRepository.findByIdentifier(ANY_IDENTIFIER)).willReturn(Mono.empty());

		given(associateRepository.save(associateDocument)).willReturn(Mono.just(associateDocument));

		VoteDocument voteDocument = VoteDocument.builder().decision(DecisionType.NO).session(sessionDocument)
				.createdAt(any()).build();

		given(associateRepository.save(associateDocument.toBuilder().votes(Arrays.asList(voteDocument)).build()))
				.willReturn(Mono.just(associateDocument.toBuilder().votes(Arrays.asList(voteDocument)).build()));

		Mono<ResponseEntity<Void>> response = this.sessionService
				.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID);

		StepVerifier.create(response).expectSubscription()
				.assertNext(expectResponseEntity -> expectResponseEntity.getStatusCode().is2xxSuccessful())
				.verifyComplete();
	}

	@Test
	public void voteButUserNotValidTest() {
		given(userInfoClient.users(ANY_IDENTIFIER)).willThrow(new RuntimeException(ANY_EXCEPTION_ERROR));

		ClientException thrown = assertThrows(ClientException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());
		assertTrue(thrown.getMessage().contains(CLIENT_API_ERROR));
	}

	@Test
	public void voteButUserIdentifierIsNotValidTest() {
		given(userInfoClient.users(ANY_IDENTIFIER)).willReturn(Mono.empty());

		ApiNoDataException thrown = assertThrows(ApiNoDataException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());
		assertTrue(thrown.getMessage().contains(IDENTIFIER_NOT_VALID));
	}

	@Test
	public void voteButUserUnableToVoteTest() {
		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.UNABLE_TO_VOTE).build()));

		ApiBusinessException thrown = assertThrows(ApiBusinessException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());

		assertTrue(thrown.getMessage().contains(USER_UNABLE_TO_VOTE));
	}

	@Test
	public void voteButThereNoSessionForUuidTest() {
		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.empty());

		ApiNoDataException thrown = assertThrows(ApiNoDataException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());

		assertTrue(thrown.getMessage().contains(NO_SESSION_FOR_THE_UUID));
	}

	@Test
	public void voteButSessionNotStartedTest() {
		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		SessionDocument sessionDocument = SessionDocument.builder().id(ANY_DOCUMENT_ID).minutesLong(MINUTES_LONG)
				.uuid(ANY_UUID).start(null).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		ApiBusinessException thrown = assertThrows(ApiBusinessException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());

		assertTrue(thrown.getMessage().contains(SESSION_NOT_STARTED));
	}

	@Test
	public void voteButSessionIsCloseTest() {
		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		Instant now = Instant.now();

		SessionDocument sessionDocument = SessionDocument.builder().id(ANY_DOCUMENT_ID).minutesLong(MINUTES_LONG)
				.uuid(ANY_UUID).start(now).end(now).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		ApiBusinessException thrown = assertThrows(ApiBusinessException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());

		assertTrue(thrown.getMessage().contains(SESSION_IS_CLOSED));
	}

	@Test
	public void voteButAssociateHasAlreadyVoteTest() {
		given(userInfoClient.users(ANY_IDENTIFIER))
				.willReturn(Mono.just(UserStatusDto.builder().status(UserVoteType.ABLE_TO_VOTE).build()));

		Instant now = Instant.now();

		SessionDocument sessionDocument = SessionDocument.builder().id(ANY_DOCUMENT_ID).minutesLong(MINUTES_LONG)
				.uuid(ANY_UUID).start(now).end(now.plus(60, ChronoUnit.HOURS)).subject(SUBJECT_DESCRIPTION).build();

		given(sessionRepository.findByUuid(ANY_UUID)).willReturn(Mono.just(sessionDocument));

		AssociateDocument associateDocument = AssociateDocument.builder().identifier(ANY_IDENTIFIER)
				.votes(Arrays.asList(
						VoteDocument.builder().session(SessionDocument.builder().id(ANY_DOCUMENT_ID).build()).build()))
				.build();

		given(associateRepository.findByIdentifier(ANY_IDENTIFIER)).willReturn(Mono.just(associateDocument));

		ApiBusinessException thrown = assertThrows(ApiBusinessException.class,
				() -> this.sessionService
						.vote(VoteDto.builder().decision(DecisionType.NO).identifier(ANY_IDENTIFIER).build(), ANY_UUID)
						.block());

		assertTrue(thrown.getMessage().contains(ASSOCIATE_HAS_ALREADY_VOTE));
	}

}
