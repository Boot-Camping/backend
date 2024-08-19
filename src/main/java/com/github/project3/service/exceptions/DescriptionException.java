package com.github.project3.service.exceptions;

public class DescriptionException extends RuntimeException {
	public DescriptionException(String message) {
		super(message);
	}

	public DescriptionException(String message, Throwable cause) {
		super(message, cause);
	}
}
