package com.poivredesiles.fundraising.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorMessage {
	
	private String subject;

	private String intro;

	private List<String> exceptions = new ArrayList<>();

	private String message;

	public void addException(String exceptionName) {
		exceptions.add(exceptionName);
	}
}
