package com.service.managevotingsessionsservice.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionInformationDto {

	@JsonProperty("sessionId")
	private UUID id;

}
