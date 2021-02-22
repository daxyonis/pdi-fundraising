package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

/**
 * Summary of sales for one seller
 * @author evita
 *
 */
@Data
public class PdiSellerRecapDTO {

	private String sellerName;
	
	private Long numPaidOrders;
	
	private String formattedTotalSales;
}
