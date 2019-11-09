package com.service.managevotingsessionsservice.document;

import java.time.ZonedDateTime;

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
	private ZonedDateTime createdAt = ZonedDateTime.now();

	private DecisionType decision;
}
