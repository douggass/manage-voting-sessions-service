package com.service.managevotingsessionsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableFeignClients
public class ManageVotingSessionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageVotingSessionsServiceApplication.class, args);
	}

}
