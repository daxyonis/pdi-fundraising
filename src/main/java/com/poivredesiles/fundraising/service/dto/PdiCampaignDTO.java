package com.poivredesiles.fundraising.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poivredesiles.fundraising.imports.ImportsUtils;

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
    
    private String leaderName;

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

    private LocalDate exportDate;
    
    private String formattedExportDate;

    private Long orderTypeId;      
    
    @JsonProperty("campaignInfo")
    public String getCampaignInfo() {
    	return String.format("%d %s - %s", organizationNum, organizationName, project);
    }
    
    @JsonProperty("orderHeaderFilename")
    public String getOrderHeaderFilename() {
        LocalDate now = ImportsUtils.convertToLocalDate(Instant.now());
    	return String.format("%d_enteteBC_%s.csv", number, ImportsUtils.formatLocalDate(now, "dd-MM-yyyy"));
    }
    
    @JsonProperty("orderDetailFilename")
    public String getOrderDetailFilename() {
        LocalDate now = ImportsUtils.convertToLocalDate(Instant.now());
    	return String.format("%d_detailBC_%s.csv", number, ImportsUtils.formatLocalDate(now, "dd-MM-yyyy"));
    }
}
