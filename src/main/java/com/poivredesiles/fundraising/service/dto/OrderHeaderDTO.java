package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.poivredesiles.fundraising.model.order.OrderStatusEnum;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.model.OrderHeader} entity.
 */
@Data
public class OrderHeaderDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private Long id;

    private String formattedCreatedDate;

    private Long orderNumber;

    private String buyerName;

    private String buyerPhone;

    private String buyerEmail;

    private String buyerNote;

    private String buyerLanguage;

    private OrderStatusEnum orderStatus;

    private String confirmationNumber;

    private String detail;

    private List<OrderItemDTO> orderItems;
    
    private String formattedTotal;

    private String formattedConfirmationDate;

    private String campaignName;

    private Long campaignNumber;

    private String sellerName;

    private String groupName;

    private String groupLeaderName;

    private String organizationName;

}
