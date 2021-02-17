package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.OrderItem} entity.
 */
@Data
public class OrderItemDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long orderNumber;

    private String productNumber;

    private Long quantity;

    private BigDecimal unitPrice;

}
