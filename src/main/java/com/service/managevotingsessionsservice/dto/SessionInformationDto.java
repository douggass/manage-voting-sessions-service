package com.service.managevotingsessionsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SessionInformationDto {

	@JsonProperty("sessionId")
	private Long id;

}
