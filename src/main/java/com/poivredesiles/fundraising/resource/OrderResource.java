package com.poivredesiles.fundraising.resource;

import java.util.List;

import lombok.Data;

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
	
	private Long sellerId;
	
	private String email;
	private AddressResource address;
	
	private List<OrderItemResource> items;
	
	/**
	 * Strip phone number from any non-numeric character
	 * and return at most the first n digits
	 * @return
	 */
	public String getStrippedPhone(int n) {
		String strippedPhone = new String(phone);
		// Remove all non-digit 
		strippedPhone = strippedPhone.replaceAll("\\D", "");
		if(strippedPhone.length() > n) {
			// Take first 10 numbers
			strippedPhone = strippedPhone.substring(0, n-1);
		}
		return strippedPhone;
	}
	
	
}
