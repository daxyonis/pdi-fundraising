package com.poivredesiles.fundraising.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.resource.ExportFileNames;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

@RestController
@RequestMapping("/api/campaign")
public class PdiCampaignController {
	
	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	@Autowired
	private LocaleResolver localeResolver;
	
	private final Logger log = LoggerFactory.getLogger(PdiCampaignController.class);
	
	@GetMapping("/")
	public List<PdiCampaignDTO> getCampaigns(@RequestParam(required = false) Boolean active,
											 @RequestParam(required = false) Boolean blocked){
		return pdiCampaignService.findAll(active, blocked);		
	}
		
	@PostMapping("/{id}/close")
	@Secured({"ROLE_ADMIN", "ROLE_CAMPAIGN_LEADER"})
	public PdiCampaignDTO close(@PathVariable Long id, HttpServletRequest request) {
		 return pdiCampaignService.close(id, localeResolver.resolveLocale(request));
	}
	
	@PostMapping("/{id}/block")
	@Secured("ROLE_ADMIN")
	public PdiCampaignDTO block(@PathVariable Long id) {
		 return pdiCampaignService.block(id);
	}
	
	@PostMapping("/{id}/export")
	@Secured("ROLE_ADMIN")
	public PdiCampaignDTO export(@PathVariable Long id, @RequestBody ExportFileNames exportFileNames) {
		log.info("Exporting campaign data to files {}", exportFileNames.toString());
		return pdiCampaignService.export(id, exportFileNames);
	}
	
}
