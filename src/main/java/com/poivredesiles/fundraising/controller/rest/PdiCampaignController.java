package com.poivredesiles.fundraising.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poivredesiles.fundraising.model.group.PdiCampaignBatchActionEnum;
import com.poivredesiles.fundraising.resource.ContactMessage;
import com.poivredesiles.fundraising.resource.EntitySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.PdiExportDataException;
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
	
	/**
	 * Get all campaigns, optionally filtered by state flags
	 * @param active	filters the campaigns with active state (active = not disabled)
	 * @param blocked   filters the campaigns with blocked state
	 * @return the list of campaigns that have given state
	 */
	@GetMapping("/")
	public List<PdiCampaignDTO> getCampaigns(@RequestParam(required = false) Boolean active,
											 @RequestParam(required = false) Boolean blocked){
		return pdiCampaignService.findAll(active, blocked);		
	}
		
	/**
	 * Close one given campaign : disables the campaign as well as all users (sellers and buyers)
	 * except for the campaign leader.
	 * @param id		campaign ID
	 * @param request	
	 * @return the closed campaign
	 */
	@PostMapping("/{id}/close")
	@Secured({"ROLE_ADMIN", "ROLE_CAMPAIGN_LEADER"})
	public PdiCampaignDTO close(@PathVariable Long id, HttpServletRequest request) {
		 return pdiCampaignService.close(id, localeResolver.resolveLocale(request));
	}


	@PostMapping("/{id}/contact")
	@Secured({"ROLE_ADMIN", "ROLE_CAMPAIGN_LEADER"})
	public void contactPdi(@PathVariable Long id, @RequestBody ContactMessage contactMessage, HttpServletRequest request) {
		pdiCampaignService.contactPdi(id, contactMessage, localeResolver.resolveLocale(request));
	}
	
	/**
	 * Block one given campaign : this puts blocked campaign state to true, and disables
	 * all users including the campaign leader.
	 * @param id	campaign ID
	 * @return the blocked campaign
	 */
	@PostMapping("/{id}/block")
	@Secured("ROLE_ADMIN")
	public PdiCampaignDTO block(@PathVariable Long id) {
		return pdiCampaignService.block(id);
	}
	
	/**
	 * Download the whole campaign order headers data as a CSV file
	 * @param id		campaign id
	 * @param filename  filename for the download data
	 * @param request	
	 * @param response
	 * @throws PdiExportDataException	if CSV export did not succeed
	 * @throws IOException				if could not get writer from http response
	 */
	@GetMapping("/{id}/download/headers")
	@Secured("ROLE_ADMIN")
	public void downloadOrderHeaders(@PathVariable Long id, @RequestParam String filename, HttpServletRequest request, HttpServletResponse response) throws PdiExportDataException, IOException {
		log.info("Exporting order headers in file {}", filename);
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		pdiCampaignService.exportHeaders(id, response.getWriter(), localeResolver.resolveLocale(request));
	}
	
	/**
	 * Download the whole campaign order details data as a CSV file
	 * @param id		campaign id
	 * @param filename	filename for the download data
	 * @param request
	 * @param response
	 * @throws PdiExportDataException	if CSV export did not succeed
	 * @throws IOException				if could not get writer from http response
	 */
	@GetMapping("/{id}/download/details")
	@Secured("ROLE_ADMIN")
	public void downloadOrderDetails(@PathVariable Long id, @RequestParam String filename, HttpServletRequest request, HttpServletResponse response) throws IOException, PdiExportDataException {
		log.info("Exporting order details in file {}", filename);
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		pdiCampaignService.exportDetails(id, response.getWriter(), localeResolver.resolveLocale(request));
	}

	/**
	 * Perform a batch action on campaigns
	 *
	 */
	@PostMapping("/batch")
	@Secured("ROLE_ADMIN")
	public void batchAction(@RequestParam String action, @RequestBody List<Long> campaignIds) {
		switch(action) {
			case "resend_recaps":
				pdiCampaignService.resendRecapClosedCampaignsWithin(campaignIds);
				break;
			default:
				throw new UnsupportedOperationException("Unknown batch action: " + action);
		}
	}
}
