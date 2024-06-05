package com.poivredesiles.fundraising.service;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import com.poivredesiles.fundraising.exception.PdiExportDataException;
import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.resource.ContactMessage;
import com.poivredesiles.fundraising.resource.EntitySelector;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;
import com.poivredesiles.fundraising.service.dto.PdiCampaignRecapDTO;

public interface PdiCampaignService {

	/**
	 * Import a list of campaigns to PDI campaign
	 * @param campaigns
	 */
	void importCampaigns(List<Campaign> campaigns) throws PdiImportDataException;

	/**
	 * Queries whether there are active PDI campaigns (not blocked, not closed)
	 * @return
	 */
	boolean thereAreActiveCampaigns();

	/**
	 * Close a PDI campaign
	 * This causes the following actions:
	 *   - User disabling : disables all campaign users except the campaign leader
	 *   - Email sending : send to the campaign leader the sales recap
	 *   - Set the campaign closed flag to true; set the campaign closed date
	 * @param id
	 * @param locale 
	 * @return
	 */
	PdiCampaignDTO close(Long id, Locale locale);

	/**
	 * Find all PDI campaigns
	 * @param active	if true, filters only the campaigns that are not closed
	 * 					if false, filters only the closed campaigns
	 * 					if null, does not filter over the closed state
	 * @param blocked   if set, filters over the blocked state
	 * @return
	 */
	List<PdiCampaignDTO> findAll(Boolean active, Boolean blocked);

	/**
	 * Block a PDI campaign
	 * This causes the following actions:
	 * 		- User disabling : disables all campaign users including the campaign leader
	 * 	    - Set the campaign blocked flag to true; set the campaign blocked date
	 * A scheduled job will run that will erase all data for blocked campaigns after 1 year
	 * @param id
	 * @return
	 */
	PdiCampaignDTO block(Long id);


	/**
	 * Builds and returns the campaign recap (summary) including all groups,
	 * sales per group and all sellers, sales per seller.
	 * @param userId connected user (must be a campaign leader otherwise an exception is thrown)
	 * @return the campaign recap DTO
	 */
	PdiCampaignRecapDTO getCampaignRecapForLeader(Long userId);

	/**
	 * Export order headers to writer
	 * @param id
	 * @param writer
	 * @throws PdiExportDataException 
	 */
	void exportHeaders(Long id, PrintWriter writer, Locale locale) throws PdiExportDataException;

	/**
	 * Export order details to writer
	 * @param id
	 * @param writer
	 * @throws PdiExportDataException 
	 */
	void exportDetails(Long id, PrintWriter writer, Locale locale) throws PdiExportDataException;

	/**
	 * Cleanup method to delete all campaign data that has been blocked for more than 1 year 
	 */
	void deleteBlockedFor1Year();

	/**
	 * Contact PDI with a message
	 * @param id
	 * @param contactMessage
	 * @param resolveLocale
	 */
    void contactPdi(Long id, ContactMessage contactMessage, Locale resolveLocale);

	/**
	 * Resend the campaign recap for the closed campaigns that match the filters
	 * @param campaignIds	the campaign IDs to resend the recap for
	 * @return the list of campaign DTOs the recap of which have been resent
	 */
	List<PdiCampaignDTO> resendRecapClosedCampaignsWithin(List<Long> campaignIds);
}
