package com.github.project3.service.exceptions;

public class CategoryException extends RuntimeException {
	public CategoryException(String message) {
		super(message);
	}

	public CategoryException(String message, Throwable cause) {
		super(message, cause);
	}
}
