package com.poivredesiles.fundraising.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

@RestController
@RequestMapping("/api/campaign")
public class PdiCampaignController {
	
	@Autowired
	private PdiCampaignService pdiCampaignService;

	@GetMapping("/")
	public List<PdiCampaignDTO> getCampaigns(@RequestParam(required = false) boolean active){
		return pdiCampaignService.findAll(active);		
	}
		
	@PostMapping("/{id}/close")
	@Secured("ROLE_ADMIN")
	public PdiCampaignDTO close(@PathVariable Long id) {
		 return pdiCampaignService.close(id);
	}
	
	@PostMapping("/{id}/block")
	@Secured("ROLE_ADMIN")
	public PdiCampaignDTO block(@PathVariable Long id) {
		 return pdiCampaignService.block(id);
	}
	
}
