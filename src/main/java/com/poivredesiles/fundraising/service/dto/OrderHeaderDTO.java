package com.poivredesiles.fundraising.service.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.OrderHeader} entity.
 */
@Data
public class OrderHeaderDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

    private String formattedCreatedDate;

    private Long orderNumber;

    private String buyerName;

    private String buyerPhone;

    private String buyerEmail;

    private String buyerNote;

    private String buyerLanguage;

    private String orderStatus;

    private String confirmationNumber;

    private String detail;

    private List<OrderItemDTO> orderItems;
    
    private String formattedTotal;

    private String formattedConfirmationDate;

    private String formattedCancelDate;

    private String campaignName;

    private Long campaignNumber;

    private String sellerName;

    private String groupName;

    private String groupLeaderName;

    private String organizationName;

}
