package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;

import com.poivredesiles.fundraising.model.order.OrderStatusEnum;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.OrderHeader} entity.
 */
@Data
public class OrderHeaderDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long orderNumber;

    private String buyerName;

    private String buyerPhone;

    private String buyerNote;

    private String buyerLanguage;

    private OrderStatusEnum orderStatus;

    private String confirmationNumber;    
    
    private String detail;
    
    private String formattedTotal;
}
