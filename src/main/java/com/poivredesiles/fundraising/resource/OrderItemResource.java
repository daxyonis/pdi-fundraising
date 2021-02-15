package com.poivredesiles.fundraising.resource;

import lombok.Data;

@Data
/**
 * Resource representing a buyer order item
 * id: product id
 * qty: desired quantity of the product
 * @author evita
 *
 */
public class OrderItemResource {

	private Long id;
	private Long qty;
}
