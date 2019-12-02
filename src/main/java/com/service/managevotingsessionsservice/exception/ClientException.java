package com.service.managevotingsessionsservice.exception;

@SuppressWarnings("serial")
public class ClientException extends RuntimeException{
	public ClientException(String message) {
		super(message);
	}
}
