package com.service.managevotingsessionsservice.document;

import java.time.ZonedDateTime;

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

	private ZonedDateTime start;
	private ZonedDateTime end;

	@NotNull
	private String subject;

	@NotNull
	@Builder.Default
	private Integer minutesLong = 2;

	@NotNull
	@Builder.Default
	private ZonedDateTime createdAt = ZonedDateTime.now();
}
