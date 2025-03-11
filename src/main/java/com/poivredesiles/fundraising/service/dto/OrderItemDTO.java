package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.order.OrderItem} entity.
 */
@Data
public class OrderItemDTO implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long orderNumber;

    private String labelNumber;

    private String nameFr;

    private String nameEn;

    private Long quantity;

    private BigDecimal unitPrice;

    private String formattedUnitPrice;

    private String formatFr;

    private String formatEn;

}
