package com.poivredesiles.fundraising.imports.dto;

import lombok.Data;

@Data
public class Group {

	private Long number;
	
	private String name;
	
	private String leaderNumber;		
	
	private Long campaignNumber;

	public boolean valid() {
		if(number == null) {
			return false;
		}
		return true;
	}
}
