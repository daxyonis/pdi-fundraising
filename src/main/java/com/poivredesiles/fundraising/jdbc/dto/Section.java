package com.poivredesiles.fundraising.jdbc.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Section {

	private Long number;
	
	private BigDecimal unitPrice;
	
	private String sectionEn;
	
	private String sectionFr;
}
