package com.service.managevotingsessionsservice.dto;

import com.service.managevotingsessionsservice.type.DecisionType;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class VoteDto {

	@NonNull
	private String identifier;

	@NonNull
	private DecisionType decision;

}
