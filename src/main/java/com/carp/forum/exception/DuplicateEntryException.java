package com.carp.forum.exception;

public class DuplicateEntryException extends Exception {

	public DuplicateEntryException() {
		super("Duplicate row on unique constraint");
	}

	public DuplicateEntryException(String message) {
		super(message);
	}

}
