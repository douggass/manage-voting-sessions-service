package com.service.managevotingsessionsservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.service.managevotingsessionsservice.exception.ApiBusinessException;
import com.service.managevotingsessionsservice.exception.ApiDataBaseException;
import com.service.managevotingsessionsservice.exception.ApiNoDataException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Database Error")
	@ExceptionHandler(ApiDataBaseException.class)
	public void handleDabaseErrorException(ApiDataBaseException e) {
		log.error("Database Error: {}", e);
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No data")
	@ExceptionHandler(ApiNoDataException.class)
	public void handleEmptyFieldException(ApiNoDataException e) {
		log.error("No data: {}", e);
	}

	@ResponseBody
	@ExceptionHandler(ApiBusinessException.class)
	public ResponseEntity<?> handleBusinessException(ApiBusinessException e) {
		log.error("Business exception: {}", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

}