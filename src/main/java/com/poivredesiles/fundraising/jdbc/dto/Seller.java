package com.poivredesiles.fundraising.jdbc.dto;

import lombok.Data;

@Data
public class Seller {

	private Long number;
	
	private String name;
	
	private String buyerCode;
	
	private String campaignCode;
	
	private String password;
	
	private String authorization;
	
}
