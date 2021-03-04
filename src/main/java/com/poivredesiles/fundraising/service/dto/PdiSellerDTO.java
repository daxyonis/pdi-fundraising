package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiSeller} entity.
 */
@Data
public class PdiSellerDTO implements Serializable {
    
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private String name;

    private Long meId;

    private Long buyerId;

    private Long pdiGroupId;
    
    private Long pdiGroupNumber;
    
    private String pdiGroupName;     
       
    private String formattedPdiCampaignDueDate;
    
    private String pdiCampaignOrganization;
    
    private String pdiCampaignProject;
    
    private boolean pdiCampaignClosed;
    
    private String formattedOrdersTotal;
    
    private Long numOrders;
    
    private boolean dueDateArrived;
    
    private Integer numGroups;
}
