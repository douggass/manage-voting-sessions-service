package com.service.managevotingsessionsservice.exception;

@SuppressWarnings("serial")
public class ApiBusinessException extends RuntimeException {

	public ApiBusinessException(String message) {
		super(message);
	}

}
