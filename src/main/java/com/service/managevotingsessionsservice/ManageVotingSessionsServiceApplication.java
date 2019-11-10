package com.service.managevotingsessionsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancerAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication(exclude = ReactiveLoadBalancerAutoConfiguration.class)
@EnableReactiveMongoRepositories
@EnableReactiveFeignClients
public class ManageVotingSessionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManageVotingSessionsServiceApplication.class, args);
	}

}
