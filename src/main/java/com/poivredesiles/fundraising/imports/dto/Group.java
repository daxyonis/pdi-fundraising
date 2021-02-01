package com.poivredesiles.fundraising.imports.dto;

import com.poivredesiles.fundraising.exception.PdiImportDataException;

import lombok.Data;

@Data
public class Group {

	private Long number;
	
	private String name;
	
	private String leaderNumber;		
	
	private String campaignNumber;

	public void validate() {
		if(number == null) {
			throw new PdiImportDataException("Invalid Group entry");
		}
		
	}
}
