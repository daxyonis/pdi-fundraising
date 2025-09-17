package com.poivredesiles.fundraising.resource;

import lombok.Data;

import java.util.List;

/**
 * Resource representing a buyer order
 * @author evita
 *
 */
@Data
public class OrderResource {

	private String name;	
	private String phone;		
	private String note;
	private String email;

	private String token;
	
	private List<OrderItemResource> items;
	
	/**
	 * Strip phone number from any non-numeric character
	 * and return at most the first n digits
	 * @return
	 */
	public String getStrippedPhone(int n) {		
		// Remove all non-digit 
		String strippedPhone = phone.replaceAll("\\D", "");
		if(strippedPhone.length() > n) {
			// Take first 10 numbers
			strippedPhone = strippedPhone.substring(0, n-1);
		}
		return strippedPhone;
	}
	
	
}
