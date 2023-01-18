package com.carp.forum.exception;

/**
 * Thrown when a user tries to update an object but puts different id values in url and requestBody.
 * @author nicolas
 *
 */
public class InvalidUpdateException extends Exception {

	public InvalidUpdateException() {
		super();
	
	}

	public InvalidUpdateException(String message) {
		super(message);
	
	}

}
