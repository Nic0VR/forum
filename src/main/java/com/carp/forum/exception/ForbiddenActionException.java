package com.carp.forum.exception;

public class ForbiddenActionException extends Exception {

	public ForbiddenActionException() {
		super("Unauthorized");
	}

	public ForbiddenActionException(String message) {
		super(message);
	}

	
}
