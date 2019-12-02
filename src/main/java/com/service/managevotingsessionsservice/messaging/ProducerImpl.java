package com.service.managevotingsessionsservice.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
public class ProducerImpl implements Producer {

	@Value("${cloudkarafka.topic}")
	private String topic;

	@Autowired
	private KafkaTemplate<String, SessionMessageDto> kafkaTemplate;
	
	@Override
	public void send(SampleMessage message) {
		this.kafkaTemplate.send(topic, message.getMessage());
		log.info("message: {}", message);
	}
}
