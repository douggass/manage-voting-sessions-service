package com.service.managevotingsessionsservice.exception;

@SuppressWarnings("serial")
public class ClientUserNotFoundException extends RuntimeException {
	public ClientUserNotFoundException(String message) {
		super(message);
	}
}
