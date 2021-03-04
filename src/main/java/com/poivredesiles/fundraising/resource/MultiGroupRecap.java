package com.poivredesiles.fundraising.resource;

import java.util.List;

import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;

import lombok.Data;

@Data
public class MultiGroupRecap {

	private List<PdiGroupRecapDTO> pdiGroupRecaps;
	
	private Long numPaidOrders;
	
	private String formattedTotalSales;
}
