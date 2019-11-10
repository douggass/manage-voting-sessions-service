package com.service.managevotingsessionsservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.service.managevotingsessionsservice.exception.ApiDataBaseException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Database Error")
	@ExceptionHandler(ApiDataBaseException.class)
	public void handleEmptyFieldException(ApiDataBaseException e) {
		log.error("Database Error", e);
	}

}