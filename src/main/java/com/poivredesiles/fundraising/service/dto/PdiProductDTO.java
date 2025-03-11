package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.product.domain.PdiProduct} entity.
 */
@Data
public class PdiProductDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

    private String labelNumber;

    private String nameFr;

    private String nameEn;

    private String descriptionFr;

    private String descriptionEn;

    private String weight;

    private String formatFr;

    private String formatEn;

    private Long categoryId;
    
    private String categoryDescEn;
    
    private String categoryDescFr;
    
    private BigDecimal unitPrice;
    
}
