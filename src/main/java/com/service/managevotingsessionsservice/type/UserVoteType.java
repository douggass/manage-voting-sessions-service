package com.service.managevotingsessionsservice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserVoteType {
	ABLE_TO_VOTE("ABLE_TO_VOTE"), UNABLE_TO_VOTE("UNABLE_TO_VOTE");

	private String value;
}
