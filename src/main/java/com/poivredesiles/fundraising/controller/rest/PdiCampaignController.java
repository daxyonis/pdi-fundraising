package com.poivredesiles.fundraising.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

@RestController
@RequestMapping("/api/campaign")
public class PdiCampaignController {
	
	@Autowired
	private PdiCampaignService pdiCampaignService;

	@GetMapping("")
	public List<PdiCampaign> getCampaigns(@RequestParam(required = false) boolean active){
		return null;
		
	}
	
	
	// Close with id only ?
	@PostMapping("/close")
	public PdiCampaignDTO close(@RequestBody PdiCampaignDTO campaign) {
		pdiCampaignService.close(campaign);
	}
	
}
