package com.service.managevotingsessionsservice.document;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document("associate")
@Data
@Builder
public class AssociateDocument {

	@Id
	private String id;

	private List<VoteDocument> votes;

	@NotNull
	@Builder.Default
	private ZonedDateTime createdAt = ZonedDateTime.now();

}