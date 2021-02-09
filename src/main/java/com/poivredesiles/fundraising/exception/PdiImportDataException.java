package com.poivredesiles.fundraising.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class PdiImportDataException extends Exception {	
	
	private static final long serialVersionUID = 1L;
	
	public PdiImportDataException(String message) {
		super(message);
	}

}
