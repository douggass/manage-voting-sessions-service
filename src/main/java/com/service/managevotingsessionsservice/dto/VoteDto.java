package com.service.managevotingsessionsservice.dto;

import com.service.managevotingsessionsservice.type.DecisionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteDto {
	
	private DecisionType decision;
	private String sessionId;

}
