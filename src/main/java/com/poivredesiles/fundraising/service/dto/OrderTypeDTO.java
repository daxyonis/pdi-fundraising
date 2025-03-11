package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.OrderType} entity.
 */
@Data
public class OrderTypeDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private Set<PdiProductDTO> pdiProducts = new HashSet<>();
        
}
