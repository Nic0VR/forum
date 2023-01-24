package com.carp.forum.exception;

public class BadPayloadException extends Exception {

	public BadPayloadException() {
		super("Error in payload");
		
	}

	public BadPayloadException(String message) {
		super(message);

	}

}
