package com.poivredesiles.fundraising.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessage {
	
	private String subject;

	private String intro;

	private List<String> exceptions = new ArrayList<>();

	private String message;

	public ErrorMessage(String subject, String intro, String message) {
		this.subject = subject;
		this.intro = intro;
		this.message = message;
	}

	public void addException(String exceptionName) {
		exceptions.add(exceptionName);
	}
}
