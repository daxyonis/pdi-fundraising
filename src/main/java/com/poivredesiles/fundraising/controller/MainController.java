package com.poivredesiles.fundraising.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

@Controller
public class MainController extends BaseController {
	
	private final Logger log = LoggerFactory.getLogger(MainController.class);	
	
	@Autowired
	private CsvImportService csvImportService;
	
	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	@GetMapping("/")
	public String home(@AuthenticationPrincipal MyUserDetails userDetails, RedirectAttributes redirectAttrs) {
		log.info("Requested Home Page");
		redirectAttrs.addFlashAttribute("homeRequest", true);
		// Dispatch the home page according to the user role
		if(userDetails.hasAnyAuthority(RoleEnum.ROLE_ADMIN)) {
			// admin main page is admin dashboard
			return "redirect:/admin";
		} else if(userDetails.hasAnyAuthority(RoleEnum.ROLE_CAMPAIGN_LEADER)) {
			return "redirect:/synthese";
		} else if(userDetails.hasAnyAuthority(RoleEnum.ROLE_GROUP_LEADER)) {
			PdiSellerDTO seller = getSeller(userDetails);
			if(seller.getNumGroups() > 1) {
				// Multi-group scenario
				return "redirect:/synthese/groupes";
			} else {
				// Only 1 group
				return "redirect:/synthese/groupe/" + seller.getPdiGroupId();
			}
		} else if(userDetails.hasAnyAuthority(RoleEnum.ROLE_SELLER)) {
			return "redirect:/ventes";
		} else if(userDetails.hasAnyAuthority(RoleEnum.ROLE_BUYER)) {
			// buyer main page is order
			return "redirect:/commande";
		} else {
			throw new IllegalStateException();
		}		
	}
	
	@GetMapping("/login")
	public String login() {
		log.info("Requested Login Page");
		return "views/login";
	}
	
	@GetMapping("/admin")
	@Secured("ROLE_ADMIN")
	public String admin(Model model) {
		log.info("Requested Admin Page");				
		model.addAttribute("sectionsAndProductsLastImport", csvImportService.getSectionsAndProductsLastImportDate());
		model.addAttribute("groupsAndSellersLastImport", csvImportService.getGroupsAndSellersLastImportDate());
		model.addAttribute("showWarning", pdiCampaignService.thereAreActiveCampaigns());
		return "views/admin";
	}		
	
	@GetMapping("/produits")
	public String showProducts() {
		return "views/products";
	}
}
