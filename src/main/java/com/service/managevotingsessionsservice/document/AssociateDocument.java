package com.service.managevotingsessionsservice.document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@Document("associate")
public class AssociateDocument {

	@Id
	private String id;

	@NotNull
	private String identifier;

	private List<VoteDocument> votes;

	@NotNull
	@Builder.Default
	private Instant createdAt = OffsetDateTime.now(ZoneOffset.UTC).toInstant();

}