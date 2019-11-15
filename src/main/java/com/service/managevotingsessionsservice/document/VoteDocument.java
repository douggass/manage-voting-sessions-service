package com.service.managevotingsessionsservice.document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.service.managevotingsessionsservice.type.DecisionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteDocument {

	@DBRef
	private SessionDocument session;

	@NotNull
	@Builder.Default
	private Instant createdAt = OffsetDateTime.now(ZoneOffset.UTC).toInstant();

	private DecisionType decision;
}
