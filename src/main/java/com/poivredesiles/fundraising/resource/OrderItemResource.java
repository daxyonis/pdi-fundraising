package com.poivredesiles.fundraising.resource;

import lombok.Data;

/**
 * Resource representing a buyer order item
 * id: product id
 * qty: desired quantity of the product
 * @author evita
 *
 */
@Data
public class OrderItemResource {

	private Long id;
	private Long qty;
}
