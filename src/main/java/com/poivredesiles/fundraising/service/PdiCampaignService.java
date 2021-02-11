package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.resource.ExportFileNames;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

public interface PdiCampaignService {

	/**
	 * Import a list of campaigns to PDI campaign
	 * @param campaigns
	 */
	void importCampaigns(List<Campaign> campaigns);

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
	 * @return
	 */
	PdiCampaignDTO close(Long id);

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
	 * Export the sales results for a given PDI campaign
	 * @param id the PDI campaign id
	 * @param exportFileNames the filenames for export
	 * @return
	 */
	PdiCampaignDTO export(Long id, ExportFileNames exportFileNames);

}
