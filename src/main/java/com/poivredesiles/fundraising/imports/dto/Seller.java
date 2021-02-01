package com.poivredesiles.fundraising.imports.dto;

import lombok.Data;

@Data
public class Seller {

	private Long number;
	
	private String name;
	
	private String buyerCode;
	
	private String campaignCode;
	
	private String password;
	
	private String authorization;

	public boolean valid() {
		if(number == null || name == null || name.isBlank()) {
			return false;
		} 
		return true;
	}
	
}
