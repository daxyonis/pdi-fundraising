package com.poivredesiles.fundraising.resource;

/**
 * Enum of countries
 * code member is the  ISO03166 country code
 * https://www.iso.org/iso-3166-country-codes.html
 * 
 * @author evita
 *
 */
public enum Country {	
	CANADA ("124");
	
	private final String code;
	Country(String code){
		this.code = code;
	}
	
	public String code() { return code; }
}
