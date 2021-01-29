package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.product.domain.PdiCategory} entity.
 */
@Data
public class PdiCategoryDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private String descriptionFr;

    private String descriptionEn;

    private BigDecimal unitPrice;

    private Set<PdiProductDTO> pdiProducts = new HashSet<>();
 }
