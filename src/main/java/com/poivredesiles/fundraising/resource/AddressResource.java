package com.poivredesiles.fundraising.resource;

import lombok.Data;

@Data
public class AddressResource {

	private String line1;
	private String line2;
	private String line3;
	private String city;
	private String state;
	private String postalCode;
	private Country country;
	
}
