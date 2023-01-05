package com.poivredesiles.fundraising.imports.dto;

import lombok.Data;

@Data
public class Seller {

	private Long number;
	
	private String name;
	
	private String buyerCode;
	
	private String campaignCode;
	
	private String password;
	
	// Is Vendeur | Responsable
	private String authorization;

	public boolean valid() {
		if(number == null || name == null || name.isBlank()) {
			return false;
		} 
		return true;
	}

	public boolean hasUserInfo() {
		if(buyerCode == null || buyerCode.isBlank() ||
		   campaignCode == null || campaignCode.isBlank() ||
		   password == null || password.isBlank() ||
		   authorization == null || authorization.isBlank()) {
			return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return "Seller [number=" + number + ", name=" + name + ", authorization=" + authorization + "]";
	}

	
}
