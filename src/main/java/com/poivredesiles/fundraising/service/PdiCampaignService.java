package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

public interface PdiCampaignService {

	void importCampaigns(List<Campaign> campaigns);

	boolean thereAreActiveCampaigns();

	PdiCampaignDTO close(PdiCampaignDTO campaign);

}
