package com.service.managevotingsessionsservice.dto.userinfo;

import com.service.managevotingsessionsservice.type.UserVoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusDto {
	private UserVoteType status;
}
