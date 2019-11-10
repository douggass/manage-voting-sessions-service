package com.service.managevotingsessionsservice.exception;

public class ClientUserNotFoundException extends RuntimeException {
	public ClientUserNotFoundException(String message) {
		super(message);
	}
}
