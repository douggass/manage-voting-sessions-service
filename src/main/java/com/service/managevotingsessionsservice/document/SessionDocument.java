package com.service.managevotingsessionsservice.document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("session")
public class SessionDocument {

	@Id
	private String id;

	private UUID uuid;

	private Instant start;
	private Instant end;

	@NotNull
	private String subject;

	@NotNull
	@Builder.Default
	private Integer minutesLong = 1;

	@NotNull
	@Builder.Default
	private Instant createdAt = OffsetDateTime.now(ZoneOffset.UTC).toInstant();

	private Date deletedAt;
}
