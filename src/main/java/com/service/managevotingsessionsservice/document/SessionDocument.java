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

@Document("session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDocument {

	@Id
	private String id;

	private UUID uuid;

	private Date start;
	private Date end;

	@NotNull
	private String subject;

	@NotNull
	@Builder.Default
	private Integer minutesLong = 2;

	@NotNull
	@Builder.Default
	private Instant createdAt = OffsetDateTime.now(ZoneOffset.systemDefault()).toInstant();

	private Date deletedAt;
}
