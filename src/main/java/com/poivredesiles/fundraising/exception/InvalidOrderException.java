package com.poivredesiles.fundraising.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class InvalidOrderException extends Exception {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public InvalidOrderException(String message) {
		super(message);
	}
}
