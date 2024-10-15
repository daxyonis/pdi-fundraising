package com.poivredesiles.fundraising.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poivredesiles.fundraising.imports.ImportsUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.poivredesiles.fundraising.domain.PdiCampaign} entity.
 */
@Data
public class PdiCampaignDTO implements Serializable {
	/**
	 * 
	 */
	@Serial
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
    	return "%d %s - %s".formatted(organizationNum, organizationName, project);
    }
    
    @JsonProperty("orderHeaderFilename")
    public String getOrderHeaderFilename() {
        LocalDate now = ImportsUtils.convertToLocalDate(Instant.now());
    	return "%d_enteteBC_%s.csv".formatted(number, ImportsUtils.formatLocalDate(now, "dd-MM-yyyy"));
    }
    
    @JsonProperty("orderDetailFilename")
    public String getOrderDetailFilename() {
        LocalDate now = ImportsUtils.convertToLocalDate(Instant.now());
    	return "%d_detailBC_%s.csv".formatted(number, ImportsUtils.formatLocalDate(now, "dd-MM-yyyy"));
    }
}
