package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class OrderItemCsvDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@CsvBindByPosition(position = 0)
	private Long noCommande;
	
	@CsvBindByPosition(position = 1)
	private String noProduit;
	
	@CsvBindByPosition(position = 2)
	private Long quantite;
	
	@CsvBindByPosition(position = 3)
	private BigDecimal prixUnitaire;
	
	@CsvBindByPosition(position = 4)
	private BigDecimal sousTotal;
}
