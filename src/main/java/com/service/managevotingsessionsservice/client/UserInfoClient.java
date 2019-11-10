package com.service.managevotingsessionsservice.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.service.managevotingsessionsservice.dto.userinfo.UserStatusDto;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "user-info", url = "${host.userinfo}", decode404 = true)
public interface UserInfoClient {

	@GetMapping("/users/{identifier}")
	Mono<UserStatusDto> getUsers(@PathVariable("identifier") String identifier);

}
