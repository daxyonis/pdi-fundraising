package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiCampaign} entity.
 */
@Data
public class PdiCampaignDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long number;

    private Long organizationNum;

    private String organizationName;

    private String project;

    private String leaderNum;

    private String leaderEmail;

    private LocalDate dueDate;
    
    private String formattedDueDate;

    private Integer orderTypeNum;

    private Boolean blocked;

    private Boolean closed;

    private LocalDate blockedDate;
    
    private String formattedBlockedDate;

    private LocalDate closedDate;
    
    private String formattedClosedDate;

    private Long orderTypeId;      
}
