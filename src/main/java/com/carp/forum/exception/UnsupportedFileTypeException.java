package com.carp.forum.exception;

public class UnsupportedFileTypeException extends Exception {

	public UnsupportedFileTypeException() {
		super("this file type is not supported");
	}

	public UnsupportedFileTypeException(String message) {
		super(message);
	}

}
