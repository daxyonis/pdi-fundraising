package com.poivredesiles.fundraising.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource representing a buyer order item
 * id: product id
 * qty: desired quantity of the product
 * @author evita
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResource {

	private Long id;
	private Long qty;
}
