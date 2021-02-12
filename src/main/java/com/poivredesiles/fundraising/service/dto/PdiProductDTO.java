package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.product.domain.PdiProduct} entity.
 */
@Data
public class PdiProductDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private Long id;

    private String labelNumber;

    private String nameFr;

    private String nameEn;

    private String descriptionFr;

    private String descriptionEn;

    private String weight;

    private Long categoryId;
    
    private String categoryDescEn;
    
    private String categoryDescFr;
    
    private BigDecimal unitPrice;
    
}
