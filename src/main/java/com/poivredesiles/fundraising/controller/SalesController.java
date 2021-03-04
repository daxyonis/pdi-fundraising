package com.poivredesiles.fundraising.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.MultiGroupRecap;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.PdiGroupService;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;
import com.poivredesiles.fundraising.service.dto.PdiCampaignRecapDTO;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;

@Controller
public class SalesController extends BaseController {

	private final Logger log = LoggerFactory.getLogger(SalesController.class);
	
	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PdiGroupService pdiGroupService;
	
	/**
	 * Sales page for a seller : shows the orders for a seller
	 * @param userDetails
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_SELLER')")
	@GetMapping("/ventes")
	public String sales(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {		
		log.info("Requested Sales Page");			
		List<OrderHeaderDTO> orders = orderService.getPaidOrdersForSeller(getSeller(userDetails));		
		model.addAttribute("orders", orders);
		return "views/sales";
	}
	
	/**
	 * Summary page for a campaign leader
	 * 
	 * @param userDetails
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_CAMPAIGN_LEADER')")
	@GetMapping("/synthese")
	public String summary(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		log.info("Requested Summary Page");
		PdiCampaignRecapDTO campaignRecap = pdiCampaignService.getCampaignRecapForLeader(userDetails.getUserId());
		model.addAttribute("campaignRecap", campaignRecap);
		return "views/summary";
	}
	
	/**
	 * Summary for one group
	 * @param groupId
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_GROUP_LEADER') and @pdiGroupService.hasAccess(principal, #groupId)")
	@GetMapping("/synthese/groupe/{groupId}")
	public String groupSummary(@PathVariable Long groupId, Model model) {
		log.info("Requested group summary page");
		PdiGroupRecapDTO groupRecap = pdiGroupService.getGroupRecap(groupId);
		model.addAttribute("groupRecap", groupRecap);
		return "views/summary-group";
	}
	
	/**
	 * Summary for N groups
	 * 
	 */
	@PreAuthorize("hasRole('ROLE_GROUP_LEADER')")
	@GetMapping("/synthese/groupes")
	public String groupsSummary(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		log.info("Requested groups summary page");
		MultiGroupRecap multiGroupRecap = pdiGroupService.getMultiGroupRecapForLeader(userDetails.getUserId());
		model.addAttribute("multigroupRecap", multiGroupRecap);		
		return "views/summary-group";
	}
	
}
