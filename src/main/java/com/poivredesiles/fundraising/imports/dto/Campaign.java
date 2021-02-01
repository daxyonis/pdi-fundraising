package com.poivredesiles.fundraising.imports.dto;

import java.util.Date;

import com.poivredesiles.fundraising.exception.PdiImportDataException;

import lombok.Data;

@Data
public class Campaign {
	
	private String number;

	private String organizationNumber;
	
	private String organizationName;
	
	private String project;
	
	private String leaderNumber;
	
	private String leaderEmail;
	
	private Date dueDate;
	
	private Long numTypeBC;
	
	private String blocked;
	
	private Date closedDate;

	public void validate() {
		if(number == null || number.isEmpty()) {
			throw new PdiImportDataException("Invalid Campaign entry");
		}
			
		
	}
}
