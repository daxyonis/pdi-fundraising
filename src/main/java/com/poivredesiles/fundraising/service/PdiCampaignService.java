package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.jdbc.dto.Campaign;

public interface PdiCampaignService {

	void importCampaigns(List<Campaign> campaigns);

}
