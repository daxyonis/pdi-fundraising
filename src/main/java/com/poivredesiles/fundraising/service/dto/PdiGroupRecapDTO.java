package com.poivredesiles.fundraising.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gives the summary of sales for one group
 * @author evita
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdiGroupRecapDTO {
	
	private Long groupId;

	private String groupName;
	
	private String groupLeaderName;
	
	private Long numPaidOrders;
	
	private Long numSellers;
	
	private String formattedTotalSales;
	
	List<PdiSellerRecapDTO> pdiSellerRecaps;
}
