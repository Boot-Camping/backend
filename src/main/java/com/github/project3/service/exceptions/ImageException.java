package com.github.project3.service.exceptions;

public class ImageException extends RuntimeException {
	public ImageException(String message) {
		super(message);
	}

	public ImageException(String message, Throwable cause) {
		super(message, cause);
	}
}
