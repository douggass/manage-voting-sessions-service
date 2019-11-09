package com.service.managevotingsessionsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionCreateDto {
	private String subjectDescription;
	private Integer amountMinutes;
}
