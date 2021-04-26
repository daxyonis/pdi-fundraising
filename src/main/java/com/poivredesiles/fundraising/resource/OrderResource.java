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
}
