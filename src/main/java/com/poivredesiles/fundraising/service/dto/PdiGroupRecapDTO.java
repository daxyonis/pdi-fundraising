package com.poivredesiles.fundraising.service.dto;

import java.util.List;

import lombok.Data;

/**
 * Gives the summary of sales for one group
 * @author evita
 *
 */
@Data
public class PdiGroupRecapDTO {

	private String groupName;
	
	private Long numPaidOrders;
	
	private Long numSellers;
	
	private String formattedTotalSales;
	
	List<PdiSellerRecapDTO> pdiSellerRecaps;
}
