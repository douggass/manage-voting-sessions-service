package com.service.managevotingsessionsservice.messaging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SampleMessage {

	private Integer id;
	private SessionMessageDto message;
}