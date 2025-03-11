package com.poivredesiles.fundraising.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class PdiExportDataException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;
	
	public PdiExportDataException(String message) {
		super(message);
	}

}
