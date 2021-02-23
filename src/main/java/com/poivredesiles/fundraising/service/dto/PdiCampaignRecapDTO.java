package com.poivredesiles.fundraising.service.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Gives the summary of sales per group for a campaign
 * @author evita
 *
 */
@Data
@EqualsAndHashCode(exclude = "pdiGroupRecaps")
public class PdiCampaignRecapDTO {

	private Long campaignId;

    private Long campaignNumber;

    private Long organizationNum;

    private String organizationName;

    private String project;

    private String leaderNum;
    
    private String leaderName;        
    
    private String formattedTotalSales;
    
    private String formattedTotalProfit;
    
    private Long totalNumGroups;
    
    private Long totalNumPaidOrders;
    
    private List<PdiGroupRecapDTO> pdiGroupRecaps;
    
    private boolean closed;
    
    private String emailTo;
}
