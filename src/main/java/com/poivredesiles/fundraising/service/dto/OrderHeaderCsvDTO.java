package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class OrderHeaderCsvDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@CsvBindByPosition(position = 0)
	private Long noCommande;
	
	@CsvBindByPosition(position = 1)
	private Long noVendeur;
	
	@CsvBindByPosition(position = 2)
	private String nomAcheteur;
	
	@CsvBindByPosition(position = 3)
	private String telephone;
	
	@CsvBindByPosition(position = 4)
	private String remarque;
	
	@CsvBindByPosition(position = 5)
	private String langue;
	
	@CsvBindByPosition(position = 6)
	private BigDecimal total;
	
	@CsvBindByPosition(position = 7)
	private String etat;
	
	@CsvBindByPosition(position = 8)
	private String noConfirmation;
}
