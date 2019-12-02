package com.service.managevotingsessionsservice.exception;

@SuppressWarnings("serial")
public class ApiNoDataException extends RuntimeException {
	public ApiNoDataException(String message) {
		super(message);
	}
}
