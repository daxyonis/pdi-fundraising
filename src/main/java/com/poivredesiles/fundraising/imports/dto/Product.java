package com.poivredesiles.fundraising.imports.dto;

import lombok.Data;

/**
 * Data class for the table "Produit" 
 * @author evita
 *
 */
@Data
public class Product {

	private String number;
	
	private Long sectionNum;
	
	private String nameEn;
	
	private String nameFr;
	
	private String descEn;
	
	private String descFr;
	
	private String weight;
	
	// As seen by customers
	private String labelNumber;
	
}
