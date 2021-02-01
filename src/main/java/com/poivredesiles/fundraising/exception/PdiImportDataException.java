package com.poivredesiles.fundraising.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Bad format for import data")
public class PdiImportDataException extends RuntimeException {	
	
	private static final long serialVersionUID = 1L;
	
	public PdiImportDataException(String message) {
		super(message);
	}

}
